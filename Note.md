1. **SAGA pattern với Kafka.**
a. Kafka Config.
- Thông thường, với dự án Spring Boot, chúng ta sẽ sử dụng Auto configuration của Spring Boot để thực hiện cấu hình Kafka. Ví dụ như cấu hình key và value serializer... Tuy nhiên để linh hoạt hơn, chúng ta sẽ tự cấu hình trong KafkaConfig.java. Trong đó, chúng ta sẽ cấu hình @Bean Kafka ProducerFactory cho KafkaTemplate và @Bean Kafka ConsumerFactory cho ContainerFactory.
-> Xem lại trong KafkaConfig.java.
- Tiếp đó là việc handle exception khi Kafka Listener xử lý. Thông thường, khi có lỗi xảy ra, thì nếu service thực hiện try-catch để xử lý thì chúng ta không cần quan tâm. Nhưng nếu service không xử lý hoặc lỗi xảy ra trước khi service thực hiện thì hệ thống sẽ tự throw ra exception. Lúc này, có một thành phần DefaultErrorHandler của Spring Kafka sẽ tự động có cơ chế retry và gửi message lỗi vào một topic DLT. Chúng ta có thể sử dụng nó (retry 10 lần), nhưng thông thường thì sẽ cần cấu hình lại cho linh hoạt hơn và để giảm số lần retry xuống và tùy chỉnh DLT mong muốn.
- Chúng ta sẽ tạo một @Bean mới với return type là DefaultErrorHandler.Trong đó, chúng ta sẽ xử lý logic tùy thuộc vào kiểu message xử lý. Tiếp đó có thể tùy chỉnh lại số lần retry.
-> Xem chi tiết ở KafkaErrorHandler.java
- Sau khi cấu hình KafkaErrorHandler xong, thì cần thêm nó vào trong @KafkaListener, nếu không Listener này sẽ dùng DefaultErrorHandler mặc định của Spring Kafka.
-> Xem chi tiết ở PaymentConsumer.java

b. Triển khai Outbox pattern với Kafka.