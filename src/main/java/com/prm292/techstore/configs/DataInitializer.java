package com.prm292.techstore.configs;

import com.prm292.techstore.models.Brand;
import com.prm292.techstore.models.Category;
import com.prm292.techstore.models.Product;
import com.prm292.techstore.models.ProductImage;
import com.prm292.techstore.repositories.BrandRepository;
import com.prm292.techstore.repositories.CategoryRepository;
import com.prm292.techstore.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;

    @Override
    public void run(String... args) throws Exception {
        if (productRepository.count() > 0) {
            return; // Data already initialized
        }

        initializeData();
    }

    private void initializeData() {
        // Create Categories
        Category ramCategory = createCategory("RAM");
        Category monitorCategory = createCategory("Monitor");
        Category mouseCategory = createCategory("Mouse");
        Category keyboardCategory = createCategory("Keyboard");
        Category cpuCategory = createCategory("CPU");
        Category gpuCategory = createCategory("GPU");

        // Create Brands
        Brand corsair = createBrand("Corsair");
        Brand gskill = createBrand("G.Skill");
        Brand dell = createBrand("Dell");
        Brand asus = createBrand("ASUS");
        Brand logitech = createBrand("Logitech");
        Brand razer = createBrand("Razer");
        Brand intel = createBrand("Intel");
        Brand amd = createBrand("AMD");
        Brand nvidia = createBrand("NVIDIA");

        // === RAM PRODUCTS ===

        // Product 1: Corsair Vengeance RGB Pro
        Product ram1 = Product.builder()
                .productName("Corsair Vengeance RGB Pro 16GB (2x8GB) DDR4 3200MHz")
                .briefDescription("High-performance RGB RAM with dynamic multi-zone lighting")
                .fullDescription("Overclocked memory lights up your PC with mesmerizing dynamic multi-zone RGB lighting.")
                .technicalSpecifications(createRamSpecs("DDR4", "3200MHz", "16GB", "CL16"))
                .price(new BigDecimal("89.99"))
                // Ảnh RAM RGB phát sáng
                .primaryImageUrl("https://encrypted-tbn3.gstatic.com/shopping?q=tbn:ANd9GcRTJuws3g7_nK4fX9FW6Vj_HO5YHYGsGMPh8DW_ROS759VjfeIGf384aQIr6qTG5LsgT1Ewf_7NiZ2d5_K1M115gTZEpVwsMX9PIye4ZdtSNQfeOwGsJPcnE3Ff2mVoW6SH7tAOMw&usqp=CAc")
                .category(ramCategory)
                .brand(corsair)
                .build();
        ram1.addImage(createProductImage(ram1, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRks3hgMM96zO1Oy2y4Va4r3AKHpfRCJcDh9Q&s"));
        ram1.addImage(createProductImage(ram1, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRLni7GWjDVIjF9N_obLZhbHIBK3o4Hacr9jg&s"));
        productRepository.save(ram1);

        // Product 2: G.Skill Trident Z5
        Product ram2 = Product.builder()
                .productName("G.Skill Trident Z5 RGB 32GB (2x16GB) DDR5 6000MHz")
                .briefDescription("Premium DDR5 RAM with stunning RGB lighting")
                .fullDescription("Designed for ultra-high performance on DDR5 platforms. Featuring a sleek and streamlined metallic design.")
                .technicalSpecifications(createRamSpecs("DDR5", "6000MHz", "32GB", "CL36"))
                .price(new BigDecimal("179.99"))
                // Ảnh cận cảnh thanh RAM
                .primaryImageUrl("https://images.unsplash.com/photo-1537498425277-228ee1a47198?auto=format&fit=crop&w=800&q=80")
                .category(ramCategory)
                .brand(gskill)
                .build();
        ram2.addImage(createProductImage(ram2, "https://images.unsplash.com/photo-1628124971842-a16d8c6d1d49?auto=format&fit=crop&w=800&q=80"));
        productRepository.save(ram2);

        // === MONITOR PRODUCTS ===

        // Product 3: Dell UltraSharp
        Product monitor1 = Product.builder()
                .productName("Dell UltraSharp U2723DE 27\" 4K USB-C Hub Monitor")
                .briefDescription("27-inch 4K IPS monitor with USB-C connectivity")
                .fullDescription("Work seamlessly with a stunning 27-inch 4K monitor featuring IPS Black technology for exceptional contrast.")
                .technicalSpecifications(createMonitorSpecs("27 inch", "3840x2160", "IPS", "60Hz"))
                .price(new BigDecimal("599.99"))
                // Ảnh màn hình bàn làm việc clean setup
                .primaryImageUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS8I_iYjuT7apy3ff-Aste-kxyUjF9ESVbGFg&s")
                .category(monitorCategory)
                .brand(dell)
                .build();
        monitor1.addImage(createProductImage(monitor1, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQK8aJLkbqUbmKty737GYNNPVmanYw7gyR1_w&s"));
        productRepository.save(monitor1);

        // Product 4: ASUS ROG Swift
        Product monitor2 = Product.builder()
                .productName("ASUS ROG Swift PG279QM 27\" 240Hz Gaming Monitor")
                .briefDescription("Fast IPS gaming monitor with 240Hz refresh rate")
                .fullDescription("Designed for professional gamers and immersive gameplay. It features a 240Hz refresh rate and Fast IPS technology.")
                .technicalSpecifications(createMonitorSpecs("27 inch", "2560x1440", "Fast IPS", "240Hz"))
                .price(new BigDecimal("799.99"))
                // Ảnh màn hình Gaming Setup màu đỏ/đen
                .primaryImageUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTjs6rUolJo4EOqtHGGoQnVD1EgTuio0eCLCA&s")
                .category(monitorCategory)
                .brand(asus)
                .build();
        monitor2.addImage(createProductImage(monitor2, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQeyzrlo6HDalZKPVQsr7t_uF1Ilkrns8UoiQ&s"));
        productRepository.save(monitor2);

        // === MOUSE PRODUCTS ===

        // Product 5: Logitech G Pro X
        Product mouse1 = Product.builder()
                .productName("Logitech G Pro X Superlight Wireless")
                .briefDescription("Ultra-lightweight wireless gaming mouse")
                .fullDescription("Meticulously designed with a single goal: create the world's best gaming mouse for esports professionals.")
                .technicalSpecifications(createMouseSpecs("25600 DPI", "Wireless", "63g", "HERO 25K"))
                .price(new BigDecimal("159.99"))
                // Ảnh chuột gaming không dây
                .primaryImageUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQcgHI2geoB3UeWLHh04Dj6g54WNXv6UBplcg&s")
                .category(mouseCategory)
                .brand(logitech)
                .build();
        mouse1.addImage(createProductImage(mouse1, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQCcid2tvm_TRCQV9IJGyktfq_vs1rvhk0p7g&s"));
        productRepository.save(mouse1);

        // Product 6: Razer DeathAdder
        Product mouse2 = Product.builder()
                .productName("Razer DeathAdder V3 Pro Wireless")
                .briefDescription("Iconic ergonomic gaming mouse")
                .fullDescription("Refined with new iconic ergonomic design, featuring the Focus Pro 30K Optical Sensor Gen-2.")
                .technicalSpecifications(createMouseSpecs("30000 DPI", "Wireless", "63g", "Focus Pro 30K"))
                .price(new BigDecimal("149.99"))
                // Ảnh chuột gaming có đèn RGB
                .primaryImageUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQrUL1wcu_qJceoDxIqoFYBPXXOOoFk-W6UEQ&s")
                .category(mouseCategory)
                .brand(razer)
                .build();
        mouse2.addImage(createProductImage(mouse2, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQIwZXl3IACU2qUnsqcIgn7QmxGh4nZjHnB8A&s"));
        productRepository.save(mouse2);

        // === KEYBOARD PRODUCTS ===

        // Product 7: Corsair K70
        Product keyboard1 = Product.builder()
                .productName("Corsair K70 RGB Pro Mechanical")
                .briefDescription("Premium mechanical keyboard with Cherry MX switches")
                .fullDescription("Designed for elite performance with CHERRY MX Speed switches, per-key RGB backlighting.")
                .technicalSpecifications(createKeyboardSpecs("Mechanical", "Cherry MX", "Wired", "Full-size"))
                .price(new BigDecimal("169.99"))
                // Ảnh bàn phím cơ RGB rực rỡ
                .primaryImageUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR4GVlxivP78SuF_I6Z6JFuSLXx4O0xVSACpg&s")
                .category(keyboardCategory)
                .brand(corsair)
                .build();
        keyboard1.addImage(createProductImage(keyboard1, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRMSr7j86N7TE8NPeUQSYLYSAGcPDQpJk5JnA&s"));
        productRepository.save(keyboard1);

        // Product 8: Logitech G915
        Product keyboard2 = Product.builder()
                .productName("Logitech G915 TKL Wireless Mechanical")
                .briefDescription("Ultra-thin wireless mechanical keyboard")
                .fullDescription("Sophisticated masterpiece of design and engineering, featuring pro-grade LIGHTSPEED wireless.")
                .technicalSpecifications(createKeyboardSpecs("Mechanical", "GL Low-profile", "Wireless", "TKL"))
                .price(new BigDecimal("229.99"))
                // Ảnh bàn phím cơ low-profile clean
                .primaryImageUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSlrwuxwE5mZRvpeMX49_YID82iXvVxh2uscQ&s")
                .category(keyboardCategory)
                .brand(logitech)
                .build();
        keyboard2.addImage(createProductImage(keyboard2, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSqCskYLFefCNLMpMXWH0g6H5vf7iv_BkwnRg&s"));
        productRepository.save(keyboard2);

        // === CPU & GPU ===

        // Product 9: Intel Core i9
        Product cpu1 = Product.builder()
                .productName("Intel Core i9-13900K Desktop Processor")
                .briefDescription("24-Core (8P+16E) 13th Gen processor")
                .fullDescription("The 13th Gen Intel Core i9-13900K desktop processor delivers the fastest cores in the world.")
                .technicalSpecifications(createCpuSpecs("24 cores", "5.8 GHz", "LGA1700", "125W"))
                .price(new BigDecimal("589.99"))
                // Ảnh Chip xử lý (CPU)
                .primaryImageUrl("https://encrypted-tbn3.gstatic.com/shopping?q=tbn:ANd9GcSRdRe06P_Uxj2W_LWq7uWJjUDEGaeG1lmFxkMvF36PQxWdEDvAuP4JwVbCxAtQXVSE-VgLhgblTweDCZegSa4hTds860B-Jt7yoSybWlhYk575Jgdo5Fpccw")
                .category(cpuCategory)
                .brand(intel)
                .build();
        cpu1.addImage(createProductImage(cpu1, "https://encrypted-tbn2.gstatic.com/shopping?q=tbn:ANd9GcQ7vIljpQxrTephvAkiUFoweQ50TbczxoE6ByMburbU4-v0SE6Yxsmew7lLi0qY8ufZE3_01_o0-7RtUhj80rWFlPW9ofyOIOZHxtzByTg6"));
        productRepository.save(cpu1);

        // Product 10: NVIDIA RTX 4080
        Product gpu1 = Product.builder()
                .productName("ASUS ROG Strix GeForce RTX 4080 16GB")
                .briefDescription("High-performance graphics card with 16GB GDDR6X")
                .fullDescription("The unleashed reign of the NVIDIA Ada Lovelace architecture is here.")
                .technicalSpecifications(createGpuSpecs("16GB GDDR6X", "2610 MHz", "PCIe 4.0", "320W"))
                .price(new BigDecimal("1199.99"))
                // Ảnh Card đồ họa nằm trong case máy tính
                .primaryImageUrl("https://encrypted-tbn1.gstatic.com/shopping?q=tbn:ANd9GcQDUSWh9PEuP9uxzzDMso25gkrlBjMY4WikzriT21PU56sYnQ3wqLr97Us8hXh2cIqL6jLNiYYRMZLO4XrGSWqaQCvSdVH8bFGPa5clIQ-f")
                .category(gpuCategory)
                .brand(asus)
                .build();
        gpu1.addImage(createProductImage(gpu1, "https://encrypted-tbn1.gstatic.com/shopping?q=tbn:ANd9GcQR8fEQlh-Jd8XyeuoX3vwPWZM6HW7dhILX76l9WL0HOeJ6jbQpvRzIq33iq__37plm_5ibJW743-80yElH9rZgBomD7P-3HwtHXO9ME84AHfLMKh9JuOzrNQ"));
        productRepository.save(gpu1);

        System.out.println("✅ Successfully initialized 10 products with STABLE Unsplash images!");
    }
    private Category createCategory(String name) {
        return categoryRepository.save(Category.builder().categoryName(name).build());
    }

    private Brand createBrand(String name) {
        return brandRepository.save(Brand.builder().brandName(name).build());
    }

    private ProductImage createProductImage(Product product, String imageUrl) {
        return ProductImage.builder()
                .imageUrl(imageUrl)
                .product(product)
                .build();
    }

    private Map<String, Object> createRamSpecs(String type, String speed, String capacity, String latency) {
        Map<String, Object> specs = new HashMap<>();
        specs.put("Type", type);
        specs.put("Speed", speed);
        specs.put("Capacity", capacity);
        specs.put("Latency", latency);
        return specs;
    }

    private Map<String, Object> createMonitorSpecs(String size, String resolution, String panel, String refreshRate) {
        Map<String, Object> specs = new HashMap<>();
        specs.put("Screen Size", size);
        specs.put("Resolution", resolution);
        specs.put("Panel Type", panel);
        specs.put("Refresh Rate", refreshRate);
        return specs;
    }

    private Map<String, Object> createMouseSpecs(String dpi, String connection, String weight, String sensor) {
        Map<String, Object> specs = new HashMap<>();
        specs.put("Max DPI", dpi);
        specs.put("Connection", connection);
        specs.put("Weight", weight);
        specs.put("Sensor", sensor);
        return specs;
    }

    private Map<String, Object> createKeyboardSpecs(String type, String switches, String connection, String size) {
        Map<String, Object> specs = new HashMap<>();
        specs.put("Type", type);
        specs.put("Switches", switches);
        specs.put("Connection", connection);
        specs.put("Size", size);
        return specs;
    }

    private Map<String, Object> createCpuSpecs(String cores, String maxSpeed, String socket, String tdp) {
        Map<String, Object> specs = new HashMap<>();
        specs.put("Cores/Threads", cores);
        specs.put("Max Turbo Speed", maxSpeed);
        specs.put("Socket", socket);
        specs.put("TDP", tdp);
        return specs;
    }

    private Map<String, Object> createGpuSpecs(String memory, String boostClock, String interface_, String tdp) {
        Map<String, Object> specs = new HashMap<>();
        specs.put("Memory", memory);
        specs.put("Boost Clock", boostClock);
        specs.put("Interface", interface_);
        specs.put("TDP", tdp);
        return specs;
    }
}