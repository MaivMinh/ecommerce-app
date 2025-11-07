package com.minh.product_service.command.aggregate;

import com.minh.product_service.command.commands.CreateCategoryCommand;
import com.minh.product_service.command.commands.DeleteCategoryCommand;
import com.minh.product_service.command.commands.UpdateCategoryCommand;
import com.minh.product_service.command.events.CategoryCreatedEvent;
import com.minh.product_service.command.events.CategoryDeletedEvent;
import com.minh.product_service.command.events.CategoryUpdatedEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

@Aggregate
@Slf4j
@Getter
@Setter
public class CategoryAggregate {
    @AggregateIdentifier
    private String id;
    private String parentId;
    private String name;
    private String description;
    private String slug;
    private String image;

    public CategoryAggregate() {
        ///  default constructor required by Axon Framework.
    }

    @CommandHandler
    public CategoryAggregate(CreateCategoryCommand command) {
        log.info("Handling CreateCategoryCommand: {}", command);
        CategoryCreatedEvent event = new CategoryCreatedEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);  /// this will trigger the event sourcing handler and projection to handle this event.
    }

    @EventSourcingHandler
    public void on(CategoryCreatedEvent event) {
        log.info("Handling CategoryCreatedEvent: {}", event);
        this.id = event.getId();
        this.parentId = event.getParentId();
        this.name = event.getName();
        this.description = event.getDescription();
        this.image = event.getImage();
    }

    @CommandHandler
    public void handle(UpdateCategoryCommand command) {
        log.info("Handling UpdateCategoryCommand: {}", command);
        CategoryUpdatedEvent event = new CategoryUpdatedEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(CategoryUpdatedEvent event) {
        this.parentId = event.getParentId();
        this.name = event.getName();
        this.description = event.getDescription();
        this.image = event.getImage();
        this.slug = event.getSlug();
    }

    @CommandHandler
    public void handle(DeleteCategoryCommand command) {
        log.info("Handling DeleteCategoryCommand: {}", command);
        CategoryDeletedEvent event = new CategoryDeletedEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(CategoryDeletedEvent event) {
        log.info("Handling CategoryDeletedEvent: {}", event);
    }
}
