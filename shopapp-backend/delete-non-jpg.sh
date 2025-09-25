#!/bin/bash

# Thư mục cần kiểm tra, có thể thay đổi thành thư mục bạn muốn
DIRECTORY="D:\data\training\udemy\spring-angular\practice\shopapp-backend\uploads"

# Kiểm tra thư mục có tồn tại hay không
if [ ! -d "$DIRECTORY" ]; then
  echo "Thư mục $DIRECTORY không tồn tại."
  exit 1
fi

# Thực hiện xóa các file không có đuôi .jpg
find "$DIRECTORY" -type f ! -name "*.jpg" -exec rm -v {} \;

echo "Đã xóa tất cả các file không có đuôi .jpg trong thư mục $DIRECTORY."