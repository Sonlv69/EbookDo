# Mô tả project(Project description)
## **TRIỂN KHAI HỆ THỐNG**
### **1. GIAO DIỆN ỨNG DỤNG**

![Màn hình Home](https://lh3.googleusercontent.com/d/1Y6VMxpzJwAlk93cLGFZKh0U33AyvM8v2)		

**Màn hình Home**

![Màn hình nhập liệu search(hiển thị lịch sử tìm kiếm)](https://lh3.googleusercontent.com/d/1v1bOn54niuTKyIbfbeC2H0Nbvod_A_Gh)	

**Màn hình nhập liệu search(hiển thị lịch sử tìm kiếm)**

![Màn hình Search](https://lh3.googleusercontent.com/d/1Q3Mk2YDjb0TeOAN7vokWHFwX2BiLenW6)	

**Màn hình Search**

![Màn hình Download(hiển thị các file sách tải về)](https://lh3.googleusercontent.com/d/1YzgpvQKN1Ypk4wcowt15yB4OYZwi8CqT)	

**Màn hình Download(hiển thị các file sách tải về)**

![Màn hình chi tiết của sách(hỗ trợ tính năng tải sách)](https://lh3.googleusercontent.com/d/1hq8jzUPxch4A-hVFrwLWSFhnsLU9YO0K)	

**Màn hình Download(hiển thị các file sách tải về)**

### **2. CÁC TÍNH NĂNG CỦA ỨNG DỤNG**
- Hiển thị danh sách xem trước các quyển sách tại trang chủ ở màn hình Home(tab Home)
- Xem chi tiết từng quyển sách.
- Tải sách về dạng file epub.
- Tìm kiếm sách.
- Lưu lịch sử tìm kiếm và hiển thị lịch sử khi tìm kiếm.
- Hiển thị các file sách đã tải về và hỗ trợ mở sách qua các phần mềm đọc file epub.
  
**NGUYÊN LÝ HOẠT ĐỘNG**

![Sơ đồ nguyên lý)](https://lh3.googleusercontent.com/d/1ZO5gvN0fUtjfvK0RKLpKCllwfRr8W-ZZ)	

## **THIẾT KẾ HỆ THỐNG**
### **1. THUẬT TOÁN XỬ LÝ**
	**1.1 Thuật toán xử lý chung**
	Ứng dụng sẽ phân tích dữ liệu từ 1 trang web tải sách miễn phí. Sau khi phân tích và lấy dữ liệu về sẽ hiển thị những dữ liệu cần thiết cho từng quyển sách và link tải của quyển sách sẽ được tạo thành một chức năng của ứng dụng để có thể tải sách trực tiếp từ ứng dụng. Đồng thời có thể tìm kiếm những quyển sách mà ta muốn tải về thông qua chức năng tìm kiếm của ứng dụng.
Dựa vào một thư viện mã nguồn mở Jsoup (Nó cung cấp một API rất tiện lợi để tìm nạp URL và trích xuất và thao tác với dữ liệu trang web)
Ứng dụng sẽ được lập trình bằng ngôn ngữ Java và chạy trên nền tảng Android.
Ứng dụng cần phải kết nối internet để lấy các dữ liệu từ trang web trực tuyến.
### **2. CÁC THƯ VIỆN VÀ PHẦN MỀM HỖ TRỢ**
**2.1 Thư viện Jsoup: Trình phân tích cú pháp HTML**
	Jsoup là một thư viện Java dùng để làm việc với HTML của một trang web trực tuyến. Nó cung cấp một API rất thuận tiện để tìm nạp URL, giúp trích xuất và thao tác dữ liệu với trang web, sử dụng các phương thức DOM HTML5 và CSS selectors một cách tốt nhất.
	Jsoup là một dự án mã nguồn mở được phân phối theo giấy phép tự do MIT. Mã nguồn có sẵn tại GitHub.
**2.2 Android Studio**
**2.3 Project Gutenberg.org**
	Project Gutenberg là một thư viện sách trực tuyến hoàn toàn miễn phí. Trang hỗ trợ nhiều thể loại sách và nhiều định dạng sách điện tử như Kindle và epub miễn phí, có thể tải xuống hoặc đọc trực tuyến. Ta sẽ tìm thấy nhiều tác phẩm văn học tuyệt vời của thế giới tại đây, chủ yếu là các tác phẩm cũ mà bản quyền của Mỹ đã hết hạn. Trang web được đóng góp bởi hàng nghìn tình nguyện viên đam mê  sách điện tử đã số hóa rất nhiều đầu sách để người đọc thưởng thức hoàn.
	Lý do chọn trang web này để làm nguồn phát triển ứng dụng:
	Không có lệ phí hay cần phải đăng ký! Mọi thứ từ Project Gutenberg nhẹ nhàng và hoàn toàn miễn phí cho người đọc. Quan trọng hơn các liên kết tải xuống của các file sách đều là liên kết trực tiếp, không có quảng cáo hay link rút gọn là một lợi thế rất lớn. Đây là những điều quan trọng và thuận lợi để chúng ta chọn trang web này làm nguồn dữ liệu cho ứng dụng.


