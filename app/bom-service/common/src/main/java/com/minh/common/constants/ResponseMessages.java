package com.minh.common.constants;

public class ResponseMessages {
    public static final String SUCCESS = "Thành công";
    public static final String ERROR = "Có lỗi xảy ra";
    public static final String NOT_FOUND = "Không tìm thấy dữ liệu";
    public static final String BAD_REQUEST = "Yêu cầu không hợp lệ";
    public static final String UNAUTHORIZED = "Người dùng chưa xác thực";
    public static final String FORBIDDEN = "Không được quyền truy cập";
    public static final String INTERNAL_SERVER_ERROR = "Hệ thống đang gặp sự cố";
    public static final String VALIDATION_ERROR = "Xác thực thất bại";
    public static final String CREATED = "Tạo mới dữ liệu thành công";

    private ResponseMessages() {
        // Prevent instantiation
    }
}
