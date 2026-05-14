# Makefile at backend/Makefile

ROOT_DIR := $(shell pwd)
APP_DIR := $(ROOT_DIR)/app

# Registry và tag có thể override khi gọi make
REGISTRY ?= maivanminh
TAG ?= 1.0

SERVICES := api-gateway service-discovery product-service order-service payment-service support-service notify-service event-service realtime-gateway

COMPOSE_FILE := $(ROOT_DIR)/deployment/backend/docker-compose.yml

.PHONY: help build build-jars build-images up down restart logs ps clean all

help:
	@echo "Targets:"
	@echo "  build-jars   Build all Spring Boot jars"
	@echo "  build-images Build docker images for all services"
	@echo "  up           docker compose up -d"
	@echo "  down         docker compose down"
	@echo "  restart      down then up"
	@echo "  logs         docker compose logs -f"
	@echo "  ps           docker compose ps"
	@echo "  clean        mvn clean for all services"
	@echo "  all          build-jars + build-images + up"

build-jars:
	@for svc in $(SERVICES); do \
		echo "==> Building $$svc"; \
		cd $(APP_DIR)/$$svc && ./mvnw -DskipTests package; \
	done

.PHONY: push-images push-all
build-images:
	@for svc in $(SERVICES); do \
		echo "==> Building image $$svc"; \
		docker buildx build --no-cache --platform linux/amd64 -t maivanminh/$$svc:1.0 $(APP_DIR)/$$svc; \
	done


push-images:
	@for svc in $(SERVICES); do \
		echo "==> Pushing image $$svc"; \
		docker push $(REGISTRY)/$$svc:$(TAG); \
	done


up:
	docker compose -f $(COMPOSE_FILE) up -d

down:
	docker compose -f $(COMPOSE_FILE) down

restart: down up

logs:
	docker compose -f $(COMPOSE_FILE) logs -f

ps:
	docker compose -f $(COMPOSE_FILE) ps

.PHONY: clean-images
clean-images:
	@for svc in $(SERVICES); do \
		echo "==> Removing image: $$svc"; \
		docker rmi maivanminh/$$svc:1.0 || true; \
	done



# Nếu muốn build rồi push trong 1 lệnh
push-all: build-images push-images

all: build-jars build-images up
