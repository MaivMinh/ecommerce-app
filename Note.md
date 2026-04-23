1. **SAGA pattern với Kafka.**
a. Kafka Config.
- Thông thường, với dự án Spring Boot, chúng ta sẽ sử dụng Auto configuration của Spring Boot để thực hiện cấu hình Kafka. Ví dụ như cấu hình key và value serializer... Tuy nhiên để linh hoạt hơn, chúng ta sẽ tự cấu hình trong KafkaConfig.java. Trong đó, chúng ta sẽ cấu hình @Bean Kafka ProducerFactory cho KafkaTemplate và @Bean Kafka ConsumerFactory cho ContainerFactory.
-> Xem lại trong KafkaConfig.java.
- Tiếp đó là việc handle exception khi Kafka Listener xử lý. Thông thường, khi có lỗi xảy ra, thì nếu service thực hiện try-catch để xử lý thì chúng ta không cần quan tâm. Nhưng nếu service không xử lý hoặc lỗi xảy ra trước khi service thực hiện thì hệ thống sẽ tự throw ra exception. Lúc này, có một thành phần DefaultErrorHandler của Spring Kafka sẽ tự động có cơ chế retry và gửi message lỗi vào một topic DLT. Chúng ta có thể sử dụng nó (retry 10 lần), nhưng thông thường thì sẽ cần cấu hình lại cho linh hoạt hơn và để giảm số lần retry xuống và tùy chỉnh DLT mong muốn.
- Chúng ta sẽ tạo một @Bean mới với return type là DefaultErrorHandler.Trong đó, chúng ta sẽ xử lý logic tùy thuộc vào kiểu message xử lý. Tiếp đó có thể tùy chỉnh lại số lần retry.
-> Xem chi tiết ở KafkaErrorHandler.java
- Sau khi cấu hình KafkaErrorHandler xong, thì cần thêm nó vào trong @KafkaListener, nếu không Listener này sẽ dùng DefaultErrorHandler mặc định của Spring Kafka.
-> Xem chi tiết ở PaymentConsumer.java

2. **Triển khai OAuth2 + OIDC**
a. Cấu hình Keycloak.
- Ở phần này, thì nên chú ý mục _Valid redirect URIs_ và _Valid post logout redirect URIs_, chú ý phải có phần wildcard * ở cuối, nếu không sẽ gặp lỗi khi đăng nhập hoặc đăng xuất. Ví dụ: http://localhost:8080/*.
b. Sự khác biệt của access_token & id_token trong Keycloak.
- Trong Keycloak, nhìn chung các thông tin được extract từ 2 loại token này khá giống nhau và thông thường là access_token = id_token + roles/ permissions.
- Tuy nhiên, vẫn phải luôn tách biệt vai trò rõ ràng của 2 loại này:
- access_token: được sử dụng để xác thực và ủy quyền truy cập vào các tài nguyên bảo vệ. Nó chứa thông tin về người dùng, vai trò, và các quyền hạn mà người dùng có. Access token thường được gửi trong header của các yêu cầu HTTP để truy cập vào các API hoặc dịch vụ.
- Nói cách khác: access token cho phép request có được phép đi vào một tài nguyên nào đó hay không, và request này có quyền hạn nào (gọi được/ sử dụng được các API nào).
- id_token: được sử dụng với mục đích nhận diện người dùng. Nó chứa thông tin về người dùng như tên, email, và các thuộc tính khác. ID token thường được sử dụng trong quá trình đăng nhập để xác định danh tính của người dùng và cung cấp thông tin về người dùng cho ứng dụng. ID Token thường được sử dụng chủ yếu ở phía Client, Client web thường dùng thông tin này để tạo mới một hồ sơ/ record cho hệ thống hoặc để hiển thị thông tin người dùng trên giao diện.
- Nói cách khác: id token chỉ đơn thuần là để nhận diện người dùng, nó không có quyền hạn gì cả, và nó cũng không được sử dụng để truy cập vào tài nguyên nào cả. Nó chỉ đơn thuần là một token chứa thông tin về người dùng, và được sử dụng để xác định danh tính của người dùng.
- Trong một số trường hợp, nếu hệ thống có yêu cầu về việc xác thực và ủy quyền truy cập vào các tài nguyên bảo vệ, thì chúng ta sẽ sử dụng access_token để xác thực và ủy quyền truy cập vào các tài nguyên bảo vệ. Còn nếu hệ thống chỉ cần xác định danh tính của người dùng, thì chúng ta sẽ sử dụng id_token để xác định danh tính của người dùng. 