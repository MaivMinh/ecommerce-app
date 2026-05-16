# Makefile (ở thư mục backend/)

SERVICES := simulator-service realtime-gateway tracking-service

.PHONY: clean-images
clean-images:
	@for svc in $(SERVICES); do \
		echo "==> Removing image: $$svc"; \
		docker rmi maivanminh/$$svc:1.0 || true; \
	done

.PHONY: pull-images
pull-images:
	@for svc in $(SERVICES); do \
		echo "==> Pulling image: $$svc"; \
		docker pull maivanminh/$$svc:1.0; \
	done

.PHONY: compose-up
compose-up:
	@echo "==> Starting docker compose..."
	docker compose up -d

.PHONY: compose-down
compose-down:
	@echo "==> Stopping docker compose..."
	docker compose down

.PHONY: restart
restart: compose-down compose-up

.PHONY: deploy
deploy: pull-images compose-up