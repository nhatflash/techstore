package com.prm292.techstore.services;


import com.prm292.techstore.constants.CartStatus;
import com.prm292.techstore.common.mappers.ResponseMapper;
import com.prm292.techstore.dtos.requests.AdjustProductQuantityInCartRequest;
import com.prm292.techstore.dtos.requests.ManageProductToCartRequest;
import com.prm292.techstore.dtos.responses.CartResponse;
import com.prm292.techstore.exceptions.BadRequestException;
import com.prm292.techstore.exceptions.NotFoundException;
import com.prm292.techstore.models.Cart;
import com.prm292.techstore.models.CartItem;
import com.prm292.techstore.models.Product;
import com.prm292.techstore.models.User;
import com.prm292.techstore.repositories.CartItemRepository;
import com.prm292.techstore.repositories.CartRepository;
import com.prm292.techstore.repositories.ProductRepository;
import com.prm292.techstore.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final NotificationService notificationService;


    @Transactional
    public CartResponse handleAddProductToCart(String username, ManageProductToCartRequest request) {
        User user = userRepository.findFirstByUsernameIgnoreCase(username).orElseThrow(() -> new NotFoundException("User not found."));
        Cart processingCart = cartRepository.findFirstByUserIdAndStatus(user.getId(), CartStatus.Processing).orElse(null);

        if (processingCart == null) {
            processingCart = new Cart();
            processingCart.setUser(user);
            processingCart.setTotalPrice(new BigDecimal("0.00"));
            processingCart.setStatus(CartStatus.Processing);
            cartRepository.saveAndFlush(processingCart);
        }
        BigDecimal cartItemTotalPrice;
        Product requestedProduct = productRepository.findFirstById(request.getProductId()).orElseThrow(() -> new NotFoundException("Product not found."));

        CartItem cartItem = cartItemRepository.findFirstByCartIdAndProductId(processingCart.getId(), requestedProduct.getId()).orElse(null);

        if (cartItem == null) {
            cartItem = new CartItem();
            cartItem.setCart(processingCart);
            cartItem.setProduct(requestedProduct);
            cartItem.setQuantity(request.getQuantity());
            cartItem.setPrice(requestedProduct.getPrice());
            cartItemTotalPrice = getCartItemTotalPrice(requestedProduct.getPrice(), request.getQuantity());
        } else {
            cartItemTotalPrice = getCartItemTotalPrice(requestedProduct.getPrice(), cartItem.getQuantity());
            var newCartItemTotalPrice = getCartItemTotalPrice(requestedProduct.getPrice(), request.getQuantity());
            var updatedQuantity = cartItem.getQuantity() + request.getQuantity();
            cartItemTotalPrice = cartItemTotalPrice.add(newCartItemTotalPrice);
            cartItem.setQuantity(updatedQuantity);
        }
        var total = processingCart.getTotalPrice().add(cartItemTotalPrice);
        processingCart.setTotalPrice(total);
        cartItemRepository.save(cartItem);
        cartRepository.save(processingCart);

        List<CartItem> cartItems = cartItemRepository.findByCartId(processingCart.getId());
        notificationService.updateCartBadge(username, cartItems.size());

        return ResponseMapper.mapToCartResponse(processingCart, cartItems);
    }

    @Transactional(readOnly = true)
    public CartResponse handleGetUserCart(String username) {
        User user = userRepository.findFirstByUsernameIgnoreCase(username).orElseThrow(() -> new NotFoundException("User not found."));
        Cart userCart = cartRepository.findFirstByUserIdAndStatus(user.getId(), CartStatus.Processing).orElseThrow(() -> new NotFoundException("Processing cart not found."));
        List<CartItem> cartItems = cartItemRepository.findByCartId(userCart.getId());
        return ResponseMapper.mapToCartResponse(userCart, cartItems);
    }


    @Transactional
    public CartResponse HandleAdjustProductQuantityInCart(String username, AdjustProductQuantityInCartRequest request) {
        User user = userRepository.findFirstByUsernameIgnoreCase(username).orElseThrow(() -> new NotFoundException("User not found."));
        Cart userCart = cartRepository.findFirstByUserIdAndStatus(user.getId(), CartStatus.Processing).orElseThrow(() -> new NotFoundException("Processing cart not found."));
        CartItem cartItem = cartItemRepository.findFirstByCartIdAndProductId(userCart.getId(), request.getProductId()).orElseThrow(() -> new NotFoundException("Cart item not found."));

        if (cartItem.getQuantity() == request.getQuantity()) {
            throw new BadRequestException("Product quantity is not changed.");
        }
        if (request.getQuantity() < 0 && request.getQuantity() + cartItem.getQuantity() < 0) {
            throw new BadRequestException("The requested quantity of the cart item to subtract is higher that the current item quantity in cart");
        }
        var newQuantity = cartItem.getQuantity() + request.getQuantity();
        cartItem.setQuantity(newQuantity);
        cartItemRepository.save(cartItem);

        var cartItems = cartItemRepository.findByCartId(userCart.getId());
        var total = cartItems.stream().map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add);
        userCart.setTotalPrice(total);
        cartRepository.save(userCart);

        notificationService.updateCartBadge(username, cartItems.size());

        return ResponseMapper.mapToCartResponse(userCart, cartItems);
    }

    @Transactional
    public CartResponse handleRemoveItemFromCart(String username, int cartItemId) {
        User user = userRepository.findFirstByUsernameIgnoreCase(username).orElseThrow(() -> new NotFoundException("User not found."));
        Cart userCart = cartRepository.findFirstByUserIdAndStatus(user.getId(), CartStatus.Processing).orElseThrow(() -> new NotFoundException("Processing cart not found."));

        CartItem cartItem = cartItemRepository.findFirstById(cartItemId).orElseThrow(() -> new NotFoundException("Cart item not found."));
        if (!Objects.equals(cartItem.getCart().getId(), userCart.getId())) {
            throw new BadRequestException("Cart item does not belong to the user.");
        }
        Product product = cartItem.getProduct();
        BigDecimal cartItemPrice = product.getPrice().multiply(new BigDecimal(cartItem.getQuantity()));
        userCart.setTotalPrice(userCart.getTotalPrice().subtract(cartItemPrice));
        cartRepository.save(userCart);
        cartItemRepository.delete(cartItem);
        List<CartItem> cartItems = cartItemRepository.findByCartId(userCart.getId());
        notificationService.updateCartBadge(username, cartItems.size());

        return ResponseMapper.mapToCartResponse(userCart, cartItems);
    }


    @Transactional
    public CartResponse handleRemoveEntireCart(String username) {
        User user = userRepository.findFirstByUsernameIgnoreCase(username).orElseThrow(() -> new NotFoundException("User not found."));
        Cart userCart = cartRepository.findFirstByUserIdAndStatus(user.getId(), CartStatus.Processing).orElseThrow(() -> new NotFoundException("Processing cart not found."));
        List<CartItem> cartItems = cartItemRepository.findByCartId(userCart.getId());
        userCart.setTotalPrice(new BigDecimal("0.00"));
        cartItemRepository.deleteAll(cartItems);
        cartRepository.save(userCart);
        notificationService.updateCartBadge(username, 0);

        return ResponseMapper.mapToCartResponse(userCart, new ArrayList<>());
    }

    private BigDecimal getCartItemTotalPrice(BigDecimal productPrice, int quantity) {
        return productPrice.multiply(new BigDecimal(quantity));
    }
}
