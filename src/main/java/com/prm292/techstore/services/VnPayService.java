package com.prm292.techstore.services;

import com.prm292.techstore.exceptions.BadRequestException;
import com.prm292.techstore.exceptions.NotFoundException;
import com.prm292.techstore.models.Order;
import com.prm292.techstore.repositories.PaymentRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;

@Service
public class VnPayService {

    @Value("${vnPay.tmnCode}")
    private String vnpTmnCode;

    @Value("${vnPay.hashSecret}")
    private String vnpHashSecret;

    @Value("${vnPay.baseUrl}")
    private String vnpBaseUrl;

    @Value("${vnPay.url}")
    private String vnpUrl;


    private String hmacSha512(String key, String data) {
        try {
            if (key == null || data == null) {
                throw new NotFoundException("Key or data are missing.");
            }
            final Mac hmacSha512 = Mac.getInstance("HmacSHA512");
            byte[] keyBytes = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "HmacSHA512");
            hmacSha512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmacSha512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String, String> getVnpParams(HttpServletRequest request, Order order) {
        final String vnpVersion = "2.1.0";
        final String vnpCommand = "pay";
        final String vnpReturnUrl = vnpBaseUrl + "/vnpay-return";

        String remoteIpAddress = request.getHeader("X-Forwarded-For");
        if (remoteIpAddress == null) {
            remoteIpAddress = request.getRemoteAddr();
        }

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.of("Etc/GMT+7")));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String createdDate = formatter.format(cld.getTime());
        cld.add(Calendar.MINUTE, 15);
        String expiredDate = formatter.format(cld.getTime());

        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Version", vnpVersion);
        vnpParams.put("vnp_Command", vnpCommand);
        vnpParams.put("vnp_TmnCode", vnpTmnCode);

        BigDecimal amount = order.getCart().getTotalPrice();
        vnpParams.put("vnp_Amount", String.valueOf(amount.longValue() * 100));
        vnpParams.put("vnp_CreateDate", createdDate);
        vnpParams.put("vnp_ExpireDate", expiredDate);
        vnpParams.put("vnp_CurrCode", "VND");
        vnpParams.put("vnp_IpAddr",  remoteIpAddress);
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_TxnRef", order.getId() + "-" + System.currentTimeMillis());
        vnpParams.put("vnp_OrderInfo", "Thanh toan don hang");
        vnpParams.put("vnp_OrderType", "Other");
        vnpParams.put("vnp_ReturnUrl", vnpReturnUrl);

        return vnpParams;
    }

    public String getQueryUrl(Map<String, String> vnpParams) {
        List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnpParams.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8));

                query.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8));

                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnpSecureHash = hmacSha512(vnpHashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        return vnpUrl + "?" + queryUrl;
    }

    private String hashAllFields(Map<String, String> fields) {
        List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = fields.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8));

                if (itr.hasNext()) {
                    hashData.append('&');
                }
            }
        }
        return hmacSha512(vnpHashSecret, hashData.toString());
    }

    private boolean isValidSignature(HttpServletRequest request) {
        Map<String, String> fields = new HashMap<>();

        for (Enumeration<String> e = request.getParameterNames(); e.hasMoreElements(); ) {
            String fieldName = e.nextElement();
            String fieldValue = request.getParameter(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                fields.put(fieldName, fieldValue);
            }
        }

        String vnpSecureHash = request.getParameter("vnp_SecureHash");
        fields.remove("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");

        String signValue = hashAllFields(fields);
        return signValue.equals(vnpSecureHash);
    }

    private Integer getOrderIdFromTxnRef(String txnRef) {
        int hyphenIndex = txnRef.indexOf("-");
        if (hyphenIndex == -1) {
            throw new BadRequestException("Invalid txnRef: " + txnRef);
        }
        return Integer.parseInt(txnRef.substring(0, hyphenIndex));
    }
}
