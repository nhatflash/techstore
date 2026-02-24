package com.prm292.techstore.services;


import com.prm292.techstore.constants.CartStatus;
import com.prm292.techstore.common.mappers.ResponseMapper;
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
    public CartResponse HandleAddProductToCart(String username, ManageProductToCartRequest request) {
        User user = userRepository.findFirstByUsernameIgnoreCase(username).orElseThrow(() -> new NotFoundException("User not found."));
        Cart userProcessingCart = cartRepository.findFirstByUserIdAndStatus(user.getId(), CartStatus.Processing).orElse(null);

        if (userProcessingCart == null) {
            userProcessingCart = new Cart();
            userProcessingCart.setUser(user);
            userProcessingCart.setTotalPrice(new BigDecimal("0.00"));
            userProcessingCart.setStatus(CartStatus.Processing);
            cartRepository.saveAndFlush(userProcessingCart);
        }
        BigDecimal cartTotalPrice = userProcessingCart.getTotalPrice();
        Product requestedProduct = productRepository.findFirstById(request.getProductId()).orElseThrow(() -> new NotFoundException("Product not found."));
        CartItem cartItem = cartItemRepository.findFirstByCartIdAndProductId(userProcessingCart.getId(), requestedProduct.getId()).orElse(null);

        if (cartItem == null) {
            cartItem = new CartItem();
            cartItem.setCart(userProcessingCart);
            cartItem.setProduct(requestedProduct);
            cartItem.setQuantity(request.getQuantity());
            cartItem.setPrice(requestedProduct.getPrice());
            cartTotalPrice = cartTotalPrice.add(requestedProduct.getPrice());
        } else {
            cartTotalPrice = cartTotalPrice.subtract(requestedProduct.getPrice().multiply(new BigDecimal(cartItem.getQuantity())));
            int updatedQuantity = cartItem.getQuantity() + request.getQuantity();
            BigDecimal updatedPrice = requestedProduct.getPrice().multiply(new BigDecimal(updatedQuantity));
            cartItem.setQuantity(updatedQuantity);
            cartTotalPrice = cartTotalPrice.add(updatedPrice);
        }
        userProcessingCart.setTotalPrice(cartTotalPrice);
        cartRepository.save(userProcessingCart);
        cartItemRepository.save(cartItem);

        List<CartItem> cartItems = cartItemRepository.findByCartId(userProcessingCart.getId());
        notificationService.updateCartBadge(username, cartItems.size());

        return ResponseMapper.mapToCartResponse(userProcessingCart, cartItems);
    }

    @Transactional(readOnly = true)
    public CartResponse HandleGetUserCart(String username) {
        User user = userRepository.findFirstByUsernameIgnoreCase(username).orElseThrow(() -> new NotFoundException("User not found."));
        Cart userCart = cartRepository.findFirstByUserIdAndStatus(user.getId(), CartStatus.Processing).orElseThrow(() -> new NotFoundException("Processing cart not found."));
        List<CartItem> cartItems = cartItemRepository.findByCartId(userCart.getId());
        return ResponseMapper.mapToCartResponse(userCart, cartItems);
    }


    @Transactional
    public CartResponse HandleAdjustProductQuantityInCart(String username, ManageProductToCartRequest request) {
        User user = userRepository.findFirstByUsernameIgnoreCase(username).orElseThrow(() -> new NotFoundException("User not found."));
        Cart userCart = cartRepository.findFirstByUserIdAndStatus(user.getId(), CartStatus.Processing).orElseThrow(() -> new NotFoundException("Processing cart not found."));
        Product product = productRepository.findFirstById(request.getProductId()).orElseThrow(() -> new NotFoundException("Product not found."));
        CartItem cartItem = cartItemRepository.findFirstByCartIdAndProductId(userCart.getId(), request.getProductId()).orElseThrow(() -> new NotFoundException("Cart item not found."));

        if (cartItem.getQuantity() == request.getQuantity()) {
            throw new BadRequestException("Product quantity is not changed.");
        }

        BigDecimal oldItemPrice = product.getPrice().multiply(new BigDecimal(cartItem.getQuantity()));
        BigDecimal newItemPrice = product.getPrice().multiply(new BigDecimal(request.getQuantity()));
        BigDecimal newCartTotalPrice = userCart.getTotalPrice().subtract(oldItemPrice).add(newItemPrice);
        cartItem.setQuantity(request.getQuantity());
        userCart.setTotalPrice(newCartTotalPrice);

        cartItemRepository.save(cartItem);
        cartRepository.save(userCart);

        List<CartItem> cartItems = cartItemRepository.findByCartId(userCart.getId());
        notificationService.updateCartBadge(username, cartItems.size());

        return ResponseMapper.mapToCartResponse(userCart, cartItems);
    }

    @Transactional
    public CartResponse HandleRemoveItemFromCart(String username, int cartItemId) {
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
    public CartResponse HandleRemoveEntireCart(String username) {
        User user = userRepository.findFirstByUsernameIgnoreCase(username).orElseThrow(() -> new NotFoundException("User not found."));
        Cart userCart = cartRepository.findFirstByUserIdAndStatus(user.getId(), CartStatus.Processing).orElseThrow(() -> new NotFoundException("Processing cart not found."));
        List<CartItem> cartItems = cartItemRepository.findByCartId(userCart.getId());
        userCart.setTotalPrice(new BigDecimal("0.00"));
        cartItemRepository.deleteAll(cartItems);
        cartRepository.save(userCart);
        notificationService.updateCartBadge(username, 0);

        return ResponseMapper.mapToCartResponse(userCart, new ArrayList<>());
    }
}
