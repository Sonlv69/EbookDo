# Mô tả project(Project description)
## **TRIỂN KHAI HỆ THỐNG**
### **1. CÁC TÍNH NĂNG CỦA ỨNG DỤNG**
- Hiển thị danh sách xem trước các quyển sách tại trang chủ ở màn hình Home(tab Home)
- Xem chi tiết từng quyển sách.
- Tải sách về dạng file epub.
- Tìm kiếm sách.
- Lưu lịch sử tìm kiếm và hiển thị lịch sử khi tìm kiếm.
- Hiển thị các file sách đã tải về và hỗ trợ mở sách qua các phần mềm đọc file epub.
### **2. GIAO DIỆN ỨNG DỤNG**

**Màn hình Home**

![Màn hình Home](https://lh3.googleusercontent.com/d/1Y6VMxpzJwAlk93cLGFZKh0U33AyvM8v2)		

**Màn hình nhập liệu search(hiển thị lịch sử tìm kiếm)**

![Màn hình nhập liệu search(hiển thị lịch sử tìm kiếm)](https://lh3.googleusercontent.com/d/1v1bOn54niuTKyIbfbeC2H0Nbvod_A_Gh)	

**Màn hình Search**

![Màn hình Search](https://lh3.googleusercontent.com/d/1Q3Mk2YDjb0TeOAN7vokWHFwX2BiLenW6)	

**Màn hình Download(hiển thị các file sách tải về)**

![Màn hình Download(hiển thị các file sách tải về)](https://lh3.googleusercontent.com/d/1YzgpvQKN1Ypk4wcowt15yB4OYZwi8CqT)	

**Màn hình Download(hiển thị các file sách tải về)**
  
![Màn hình chi tiết của sách(hỗ trợ tính năng tải sách)](https://lh3.googleusercontent.com/d/1hq8jzUPxch4A-hVFrwLWSFhnsLU9YO0K)	

## **NGUYÊN LÝ HOẠT ĐỘNG**

![Sơ đồ nguyên lý)](https://lh3.googleusercontent.com/d/1ZO5gvN0fUtjfvK0RKLpKCllwfRr8W-ZZ)	

## **THIẾT KẾ HỆ THỐNG**
### **1. THUẬT TOÁN XỬ LÝ**
**1.1 Thuật toán xử lý chung**
- Ứng dụng sẽ phân tích dữ liệu từ 1 trang web tải sách miễn phí. Sau khi phân tích và lấy dữ liệu về sẽ hiển thị những dữ liệu cần thiết cho từng quyển sách và link tải của quyển sách sẽ được tạo thành một chức năng của ứng dụng để có thể tải sách trực tiếp từ ứng dụng. Đồng thời có thể tìm kiếm những quyển sách mà ta muốn tải về thông qua chức năng tìm kiếm của ứng dụng.
- Dựa vào một thư viện mã nguồn mở Jsoup (Nó cung cấp một API rất tiện lợi để tìm nạp URL và trích xuất và thao tác với dữ liệu trang web)
- Ứng dụng sẽ được lập trình bằng ngôn ngữ Java và chạy trên nền tảng Android.
- Ứng dụng cần phải kết nối internet để lấy các dữ liệu từ trang web trực tuyến.
### **2. CÁC THƯ VIỆN VÀ PHẦN MỀM HỖ TRỢ**
**2.1 Thư viện Jsoup: Trình phân tích cú pháp HTML**
- Jsoup là một thư viện Java dùng để làm việc với HTML của một trang web trực tuyến. Nó cung cấp một API rất thuận tiện để tìm nạp URL, giúp trích xuất và thao tác dữ liệu với trang web, sử dụng các phương thức DOM HTML5 và CSS selectors một cách tốt nhất.
- Jsoup là một dự án mã nguồn mở được phân phối theo giấy phép tự do MIT. Mã nguồn có sẵn tại GitHub.
	
**2.2 Android Studio**

**2.3 Project Gutenberg.org**
- Project Gutenberg là một thư viện sách trực tuyến hoàn toàn miễn phí. Trang hỗ trợ nhiều thể loại sách và nhiều định dạng sách điện tử như Kindle và epub miễn phí, có thể tải xuống hoặc đọc trực tuyến. Ta sẽ tìm thấy nhiều tác phẩm văn học tuyệt vời của thế giới tại đây, chủ yếu là các tác phẩm cũ mà bản quyền của Mỹ đã hết hạn. Trang web được đóng góp bởi hàng nghìn tình nguyện viên đam mê  sách điện tử đã số hóa rất nhiều đầu sách để người đọc thưởng thức hoàn.
- Lý do chọn trang web này để làm nguồn phát triển ứng dụng:
- Không có lệ phí hay cần phải đăng ký! Mọi thứ từ Project Gutenberg nhẹ nhàng và hoàn toàn miễn phí cho người đọc. Quan trọng hơn các liên kết tải xuống của các file sách đều là liên kết trực tiếp, không có quảng cáo hay link rút gọn là một lợi thế rất lớn. Đây là những điều quan trọng và thuận lợi để chúng ta chọn trang web này làm nguồn dữ liệu cho ứng dụng.

### **3. CẤU TRÚC DỮ LIỆU VÀ PHƯƠNG THỨC SỬ DỤNG**
**3.1 Sử dụng Jsoup phân tích html một trang web**
Tạo đối tượng Document để kết nối đến trang web: 
	`Document document = (Document)Jsoup.connect(url).timeout(30000).get();`
Với: - url là liên kết đến trang web cần phân tích.
     - timeout(30000) thời gian chờ kết nối.

**Cấu trúc html cần phần tích**

![Cấu trúc html cần phần tích](https://lh3.googleusercontent.com/d/1YdYKgCnBu0fBua3yKsNLmQofZ4qDX2fa)	

Ví dụ với 1 trang html như trên, mục tiêu là cần lấy liên kết của thẻ <a href=..> sẽ được thực hiện như sau:
	
	`Elements sub = document.select("div.page_content " +
              	 			"> div.body " +
             	 	 		"> div " +
              				"> ul.results " +
              		 		"> li.booklink");`
- Câu lệnh query dữ liệu trên trang web: select hoặc selectFirst. Với select sẽ lấy được 1 tập các dữ liệu có cấu trúc giống nhau trong khi selectFirst thì lấy dữ liệu phù hợp đầu tiên.
- Lấy text của một thuộc tính trong thẻ sau khi đã query với ví dụ như html trên hình 13 ta lấy dòng chữ “Broken Barriers” của thẻ title, sau khi đã query được dữ liệu vào biến subA bằng phương thức text(): subA.text();

**Chữ Broken Barriers là tên sách cần lấy**
	
![Chữ Broken Barriers là tên sách cần lấy](https://lh3.googleusercontent.com/d/1YdYKgCnBu0fBua3yKsNLmQofZ4qDX2fa)	
	
**3.2Sử dụng Firebase để lưu lịch sử tìm kiếm lên cloud database**
Kết nối đến realtime database của Firebase: 
`//kết nối đến database
FirebaseDatabase mDatabase mDatabase =FirebaseDatabase.getInstance(); `
Tạo biến tham chiếu đến node cần xử lí:
`DatabaseReference mDatabaseReference mDatabaseReference = mDatabase.getReference().child(ten-cua-node);`
Để lưu trữ data riêng biệt cho mỗi thiết bị android, ta lấy mã bảo mật làm đại diện cho thiết bị và đặt mã đó làm tên cho root node trong database cho thiết bị đó:
`//phương thức lấy mã secure của thiết bị android
String android_id = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);`
	
**Mã của từng thiết bị riêng biệt sẽ được tạo tự động trên database khi người dùng tìm kiếm sách lần đầu tiên**

	![Sơ đồ nguyên lý)](https://lh3.googleusercontent.com/d/1ZO5gvN0fUtjfvK0RKLpKCllwfRr8W-ZZ)

Thêm dữ liệu vào database:
`mDatabaseReference.child(ten_child_node).setValue(data);`
Xóa dữ liệu khỏi database:
`mDatabaseReference.child(ten_child_node).removeValue();`
	
**Khi chạy ta sẽ được dữ liệu như thế này**
	
	![Sơ đồ nguyên lý)](https://lh3.googleusercontent.com/d/1ZO5gvN0fUtjfvK0RKLpKCllwfRr8W-ZZ)


