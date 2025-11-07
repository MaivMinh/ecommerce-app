proto:
	protoc --proto_path=./app/cart-service/src/main/proto --java_out=./app/cart-service/src/main/java --grpc-java_out=./app/cart-service/src/main/java ./app/cart-service/src/main/proto/*.proto
	protoc --proto_path=./app/product-service/src/main/proto --java_out=./app/product-service/src/main/java --grpc-java_out=./app/product-service/src/main/java ./app/product-service/src/main/proto/*.proto
	protoc --proto_path=./app/order-service/src/main/proto --java_out=./app/order-service/src/main/java --grpc-java_out=./app/order-service/src/main/java ./app/order-service/src/main/proto/*.proto
	protoc --proto_path=./app/payment-service/src/main/proto --java_out=./app/payment-service/src/main/java --grpc-java_out=./app/payment-service/src/main/java ./app/payment-service/src/main/proto/*.proto
	protoc --proto_path=./app/side-service/src/main/proto --java_out=./app/side-service/src/main/java --grpc-java_out=./app/side-service/src/main/java ./app/side-service/src/main/proto/*.proto
