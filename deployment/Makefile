# Makefile (ở thư mục backend/)

SERVICES := api-gateway service-discovery product-service order-service payment-service support-service notify-service event-service realtime-gateway

.PHONY: clean-images
clean-images:
	@for svc in $(SERVICES); do \
		echo "==> Removing image: $$svc"; \
		docker rmi maivanminh/$$svc:1.0 || true; \
	done
