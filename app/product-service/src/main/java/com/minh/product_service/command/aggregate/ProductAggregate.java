package com.minh.product_service.command.aggregate;

import com.minh.product_service.command.commands.CreateProductCommand;
import com.minh.product_service.command.commands.DeleteProductCommand;
import com.minh.product_service.command.commands.UpdateProductCommand;
import com.minh.product_service.command.events.ProductCreatedEvent;
import com.minh.product_service.command.events.ProductDeletedEvent;
import com.minh.product_service.command.events.ProductUpdatedEvent;
import com.minh.product_service.dto.ProductVariantDTO;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

import java.util.List;

@Aggregate
public class ProductAggregate {
    @AggregateIdentifier
    private String id;
    private String name;
    private String slug;
    private String description;
    private String cover;
    private List<String> images;
    private Double price;
    private Double originalPrice;
    private List<ProductVariantDTO> productVariants;
    private String status;
    private Boolean isFeatured;
    private Boolean isNew;
    private Boolean isBestseller;
    private String categoryId;
    private String errorMsg;

    public ProductAggregate() {
        // Default constructor for Axon framework
    }

    @CommandHandler
    public ProductAggregate(CreateProductCommand command) {
        ProductCreatedEvent event = new ProductCreatedEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(ProductCreatedEvent event) {
        this.id = event.getId();
        this.name = event.getName();
        this.slug = event.getSlug();
        this.description = event.getDescription();
        this.cover = event.getCover();
        this.images = event.getImages();
        this.price = event.getPrice();
        this.originalPrice = event.getOriginalPrice();
        this.productVariants = event.getProductVariants();
        this.status = event.getStatus();
        this.isFeatured = event.getIsFeatured();
        this.isNew = event.getIsNew();
        this.isBestseller = event.getIsBestseller();
        this.categoryId = event.getCategoryId();
    }

    @CommandHandler
    public void handle(UpdateProductCommand command) {
        ProductUpdatedEvent event = new ProductUpdatedEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(ProductUpdatedEvent event) {
        this.name = event.getName();
        this.slug = event.getSlug();
        this.description = event.getDescription();
        this.cover = event.getCover();
        this.images = event.getImages();
        this.price = event.getPrice();
        this.originalPrice = event.getOriginalPrice();
        this.productVariants = event.getProductVariants();
        this.status = event.getStatus();
        this.isFeatured = event.getIsFeatured();
        this.isNew = event.getIsNew();
        this.isBestseller = event.getIsBestseller();
        this.categoryId = event.getCategoryId();
    }

    @CommandHandler
    public void handle(DeleteProductCommand command) {
        ProductDeletedEvent event = new ProductDeletedEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(ProductDeletedEvent event) {
    }

}
