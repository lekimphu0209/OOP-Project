# Đề Tài 3: Hệ Thống Mô Phỏng Hệ Sinh Thái Hoang Dã (Wild-Life Eco Simulation)

## Hướng Dẫn Cài Đặt và Sử Dụng

### Cách Tải Về Project

**Option 1: Clone từ Git Repository (nếu có)**

```bash
git clone <repository-url>
cd OOP_Project
```

**Option 2: Tải trực tiếp (ZIP)**

1. Tải file ZIP từ repository
2. Giải nén vào thư mục mong muốn
3. Đổi tên thư mục thành `OOP_Project`

### Yêu Cầu Hệ Thống

- **Java JDK 17** (Eclipse Adoptium) - Đường dẫn: `C:\Program Files\Eclipse Adoptium\jdk-17.0.18.8-hotspot`
- **IDE:** Visual Studio Code
- **OS:** Windows
- **RAM:** Tối thiểu 4GB
- **Disk:** Ít nhất 500MB

### Cài Đặt Trên Visual Studio Code

**Bước 1: Cài đặt Java**

1. Tải JDK 17 từ [Eclipse Adoptium](https://adoptium.net/)
2. Cài đặt vào máy (đường dẫn: `C:\Program Files\Eclipse Adoptium\jdk-17.0.18.8-hotspot`)
3. Thêm Java vào PATH environment variable:
   - Mở Control Panel > System > Advanced system settings
   - Environment Variables > System variables > Path > Edit
   - Thêm: `C:\Program Files\Eclipse Adoptium\jdk-17.0.18.8-hotspot\bin`

**Bước 2: Cài đặt Visual Studio Code**

1. Tải VS Code từ [code.visualstudio.com](https://code.visualstudio.com/)
2. Cài đặt vào máy

**Bước 3: Cài đặt Java Extension Pack**

1. Mở Visual Studio Code
2. Vào Extensions (Ctrl+Shift+X)
3. Tìm kiếm "Extension Pack for Java"
4. Cài đặt extension (Microsoft)

**Bước 4: Mở Project**

1. File > Open Folder
2. Chọn thư mục `OOP_Project`
3. VS Code sẽ tự động nhận diện project Java

**Bước 5: Compile Project**

1. Mở terminal trong VS Code (Ctrl+`)
2. Chạy lệnh:

```bash
cd d:\OOP\OOP_Project
javac -encoding UTF-8 -d bin -cp bin src/ecosystem/**/*.java
```

**Bước 6: Chạy Project**

```bash
java -cp bin ecosystem.Main
```

Hoặc chạy trực tiếp với JDK đã cài:

```bash
cd d:\OOP\OOP_Project
"C:\Program Files\Eclipse Adoptium\jdk-17.0.18.8-hotspot\bin\java.exe" -cp bin ecosystem.Main
```

## Tóm Tắt Project

Đây là một hệ thống mô phỏng hệ sinh thái hoang dã được viết bằng Java, sử dụng OOP (Object-Oriented Programming) để mô phỏng hành vi của các loài động vật trong môi trường tự nhiên theo yêu cầu đề tài.

## Mô Tả Thế Giới

### Các Vùng Địa Hình

Hệ thống bao gồm 4 vùng địa hình chính:

1. **Đồng cỏ (Grassland):** Vùng đất bằng phẳng với nhiều cỏ
2. **Khu rừng rậm (Forest):** Vùng rừng rậm, nơi con mồi có thể trốn thoát
3. **Hồ nước (Water):** Vùng nước, nơi cá và vịt sinh sống
4. **Bản đồ tổng hợp:** Kết hợp cả 3 vùng trên trong một bản đồ

### Đặc Điểm Zoom

- Khi xem vùng hồ nước nhỏ: Các con vật (cá, vịt) hiển thị to
- Khi xem toàn bản đồ rừng: Các thực thể thu nhỏ lại để thấy sự di chuyển tổng thể

### Các Loài Thực Thể (Ít Nhất 5 Loại)

**Thực vật:**

- **Cỏ (Grass):** Tự sinh sôi theo thời gian
- **Cây ăn quả (Fruit Tree):** Tự sinh sôi theo thời gian

**Động vật ăn cỏ (Herbivore):**

- **Thỏ (Rabbit):** Hiền lành, chạy trốn khi gặp nguy hiểm
- **Hươu (Deer):** Hiền lành, chạy trốn khi gặp nguy hiểm
- **Vịt (Duck):** Có thể bơi, ăn cỏ và cây ăn quả
- **Voi (Elephant):** Động vật đặc biệt - không sợ kẻ thù, có quyền ưu tiên di chuyển

**Động vật ăn thịt (Predator):**

- **Sói (Wolf):** Săn đuổi động vật ăn cỏ, sợ hổ và người
- **Hổ (Tiger):** Săn đuổi động vật ăn cỏ, sợ người
- **Cá sấu (Crocodile):** Săn cá và động vật ăn cỏ, có thể bơi

**Động vật đặc biệt:**

- **Người (Human):** Thợ săn - ưu tiên di chuyển, không nhường đường, có thể săn tất cả các loài khác

### Chi Tiết Từng Loài

**Thực Vật**

**Cỏ (Grass)**

- **Loại:** Thực vật
- **Giá trị dinh dưỡng:** 5.0
- **Giai đoạn trưởng thành:** 0-10
- **Ăn được khi:** growthStage >= 3
- **Đặc điểm:** Tự sinh sôi theo thời gian, hồi sinh sau 50 ticks khi bị ăn
- **Ai ăn:** Rabbit, Deer, Duck, Elephant
- **Hồi sinh:** Sau 50 ticks (25 giây) khi bị ăn

**Cây ăn quả (Fruit Tree)**

- **Loại:** Thực vật
- **Giá trị dinh dưỡng:** 10.0
- **Giai đoạn trưởng thành:** 0-10
- **Ăn được khi:** growthStage >= 3
- **Đặc điểm:** Tự sinh sôi theo thời gian, hồi sinh sau 50 ticks khi bị ăn
- **Ai ăn:** Deer, Duck, Elephant, Human
- **Hồi sinh:** Sau 50 ticks (25 giây) khi bị ăn

**Động Vật Ăn Thịt (Predator)**

**Sói (Wolf) - Tier B**

- **Kích thước:** Radius 0.5, Health 100, Speed 1.5, Priority 3
- **Tốc độ khi săn:** 1.5x (2.25)
- **Tốc độ đói:** HungerRate 1
- **Chiến lược:** HunterStrategy
- **Cooldown sinh sản:** 90 ticks (4.5 giây)
- **Kẻ thù:** Tiger, Human
- **Ăn được:** Duck, Rabbit, Deer
- **Đặc điểm:** Săn đuổi con mồi, tăng tốc khi đuổi theo, không vào được rừng

**Hổ (Tiger) - Tier A**

- **Kích thước:** Radius 0.7, Health 100, Speed 1.3, Priority 4
- **Tốc độ khi săn:** 1.5x (1.95)
- **Tốc độ đói:** HungerRate 1
- **Chiến lược:** HunterStrategy
- **Cooldown sinh sản:** 120 ticks (6 giây)
- **Kẻ thù:** Human
- **Ăn được:** Duck, Rabbit, Deer
- **Đặc điểm:** Sói sợ Hổ, ưu tiên đường đi cao hơn Sói

**Cá sấu (Crocodile) - Tier A**

- **Kích thước:** Radius 0.6, Health 100, Speed 1.2, Priority 3
- **Tốc độ trong nước:** 1.2x (1.44)
- **Tốc độ trên đất:** 0.6x (0.72)
- **Tốc độ đói:** HungerRate 1
- **Chiến lược:** HunterStrategy
- **Cooldown sinh sản:** 120 ticks (6 giây)
- **Kẻ thù:** Elephant, Human
- **Ăn được:** Duck, Fish, Rabbit, Deer
- **Đặc biệt:** Có thể bơi và đi bộ, không đi quá 3 ô khỏi nước

**Người (Human) - Tier S**

- **Kích thước:** Radius 0.6, Health 100, Speed 1.0, Priority 5
- **Tốc độ khi săn:** 1.5x (1.5)
- **Chiến lược:** HunterStrategy
- **Cooldown sinh sản:** 200 ticks (10 giây)
- **Kẻ thù:** Không có
- **Ăn được:** Duck, Tiger, Wolf, Crocodile, Deer, Rabbit, Fruit Tree
- **Đặc biệt:** Không nhường đường, có thể săn tất cả các loài

**Động Vật Ăn Cỏ (Herbivore)**

**Thỏ (Rabbit) - Tier C**

- **Kích thước:** Radius 0.4, Health 100, Speed 1.2, Priority 1
- **Chiến lược:** ScaredStrategy
- **Cooldown sinh sản:** 60 ticks (3 giây)
- **Kẻ thù:** Tiger, Wolf, Crocodile, Human
- **Ăn được:** Cỏ
- **Đặc điểm:** Nhỏ nhất, dễ bị săn, chạy trốn khi gặp kẻ thù

**Hươu (Deer) - Tier C**

- **Kích thước:** Radius 0.6, Health 100, Speed 1.0, Priority 2
- **Chiến lược:** ScaredStrategy
- **Cooldown sinh sản:** 80 ticks (4 giây)
- **Kẻ thù:** Tiger, Wolf, Crocodile, Human
- **Ăn được:** Cỏ, Cây ăn quả
- **Đặc điểm:** Lớn hơn Thỏ, có thể ăn cây ăn quả

**Vịt (Duck)**

- **Kích thước:** Radius 0.4, Health 100, Speed 1.0, Priority 0
- **Tốc độ trong nước:** 1.3x (1.3)
- **Tốc độ trên đất:** 0.7x (0.7)
- **Chiến lược:** PassiveStrategy
- **Kẻ thù:** Không có
- **Ăn được:** Cỏ, Cây ăn quả
- **Đặc biệt:** Có thể bơi và đi bộ, không đi quá 4 ô khỏi nước

**Voi (Elephant) - Tier S**

- **Kích thước:** Radius 1.0, Health 100, Speed 0.8, Priority 5
- **Chiến lược:** PassiveStrategy
- **Cooldown sinh sản:** 200 ticks (10 giây)
- **Kẻ thù:** Không có
- **Ăn được:** Cỏ, Cây ăn quả
- **Đặc biệt:** Không nhường đường, không sợ kẻ thù

**Động Vật Khác**

**Cá (Fish) - Tier D**

- **Kích thước:** Radius 0.3, Health 100, Speed 0.8, Priority 0
- **Cooldown sinh sản:** 40 ticks (2 giây)
- **Kẻ thù:** Crocodile
- **Ăn được:** Không ăn gì
- **Đặc biệt:** Chỉ bơi, không đi bộ, chỉ sống trong nước

### Số Lượng Theo Mùa

- **Mùa sinh sản:** Động vật đông đúc
- **Mùa hạn hán:** Động vật ít đi

## Điều Khiển

### Chế Độ Tự Động

- Các loài tự tìm thức ăn dựa trên bản năng
- Tự tìm nước uống khi khát
- Tự săn mồi khi đói

### Chế Độ Thủ Công (Tương Lai)

- Người dùng click vào một vùng để "gieo mầm" thức ăn
- Đặt vật cản (vách đá) để chặn đường

## Tương Tác

### Hành Vi Tự Nhiên

- **Dừng khi gặp vật cản:** Động vật dừng lại khi gặp chướng ngại vật
- **Uống nước khi khát:** Động vật tự tìm nước uống khi khát

### Cơ Chế "Nhường Đường"

- Quyền ưu tiên đường đi: Elephant > Human > Tiger > Crocodile > Wolf > Deer > Rabbit > Fish
- Động vật nhỏ phải dạt sang một bên khi động vật lớn đi qua
- Thú săn mồi có quyền ưu tiên hơn con mồi

### Săn Đuổi & Vượt Mặt

- **Sói tăng tốc:** Sói có thể tăng tốc (1.5x) để đuổi kịp thỏ
- **Thỏ trốn trong rừng:** Thỏ có thể lách qua bụi rậm (nơi sói không vào được) để trốn thoát
- **Cá sấu trong nước:** Cá sấu nhanh hơn trong nước khi săn cá và vịt

### Âm Thanh (Tương Lai)

- Tiếng chim hót
- Tiếng gầm của hổ khi phát hiện con mồi
- Tiếng bước chân sột soạt trên lá khô

## Các Tính Năng Chính

### 1. Chuỗi Thức Ăn

- **Human:** Ăn Duck, Tiger, Wolf, Crocodile, Deer, Rabbit
- **Tiger:** Ăn Rabbit, Deer, Duck
- **Wolf:** Ăn Rabbit, Deer, Duck
- **Crocodile:** Ăn Fish, Rabbit, Deer, Duck
- **Duck:** Ăn Grass, Fruit Tree
- **Rabbit:** Ăn Grass
- **Deer:** Ăn Grass, Fruit Tree
- **Elephant:** Ăn Grass, Fruit Tree

### 3. Cơ Chế Săn Mồi

- **Phạm vi săn:** Predator săn khi con mồi trong phạm vi 2.0 ô
- **Tốc độ săn:** Săn thành công ngay lập tức khi vào phạm vi
- **Tăng tốc:** Predator tăng tốc 1.5x khi đuổi theo con mồi
- **Đặc biệt:** Crocodile giữ tốc độ nước (1.2x) khi săn trong nước
- **Bảo tồn:** Không săn nếu số lượng con mồi < 3 để tránh tuyệt chủng

### 4. Hành Vi AI

- **Ưu tiên:** Mệt mỏi > Khát > Đói > Sinh sản > Săn mồi > Đi lang thang
- **Predator:** Sử dụng HunterStrategy để săn mồi
- **Herbivore:** Sử dụng PassiveStrategy để tìm thức ăn
- **Escape:** Con mồi chạy trốn khi phát hiện kẻ thù

### 5. Hệ Thống Mùa

- **Mùa Xuân:** Tăng sinh sản (1.2x)
- **Mùa Hạ:** Bình thường (1.0x)
- **Mùa Thu:** Giảm sinh sản (0.8x)
- **Mùa Đông:** Ít sinh sản (0.5x), tăng damage đói

### 6. Sinh Sản

- **Điều kiện:** hunger < 30 && thirst < 30 && stamina > 70
- **Cooldown:** Fish (2s), Rabbit (3s), Deer (4s), Wolf (4.5s), Crocodile (6s), Tiger (6s), Human (10s), Elephant (10s)

### 7. Giới Hạn Quần Thể

- Rabbit: 8, Deer: 4, Fish: 8
- Wolf: 7, Tiger: 7, Crocodile: 7, Human: 5, Elephant: 5

## Yêu Cầu Kỹ Thuật (OOP Standard)

### 1. Tính Tái Sử Dụng (Extensibility)

**Dễ dàng thêm loài mới:**

- Kế thừa từ lớp `Animal` base class
- Override các method: `canEat()`, `isEnemy()`, `move()`
- Thêm strategy phù hợp (HunterStrategy, PassiveStrategy, ScaredStrategy)
- Ví dụ: Thêm Chim ưng, Cá sấu mới

**Dễ dàng thay đổi môi trường:**

- Địa hình ảnh hưởng tốc độ di chuyển:
  - **Đất (Land):** Tốc độ bình thường
  - **Nước (Water):** Cá sấu nhanh hơn (1.2x), Vịt nhanh hơn (1.3x)
  - **Rừng (Forest):** Sói không vào được, con mồi có thể trốn
- Thêm loại địa hình mới bằng cách mở rộng `TerrainType`

### 2. Hai Chế Độ Hiển thị (GUI)

**Basic Mode:**

- Các con vật là hình tròn/vuông màu sắc khác nhau
- Đỏ: Predator (Sói, Hổ, Cá sấu)
- Xanh lá: Herbivore (Thỏ, Hươu, Voi)
- Xanh dương: Động vật nước (Cá, Vịt)
- Màu vàng: Người

**Graphical Mode:**

- Sử dụng ảnh động (Gif/Sprite)
- Thể hiện hành động: chạy, ăn, nằm ngủ
- Hình ảnh được load từ thư mục `resources/`

### 3. Tách Biệt BioLogic và ViewLogic

**BioLogic (Tính toán sinh tồn):**

- `Animal.java`: Quản lý hunger, thirst, stamina, health
- `HunterStrategy.java`: Logic săn mồi
- `PassiveStrategy.java`: Logic tìm thức ăn
- `ScaredStrategy.java`: Logic chạy trốn
- `AggressiveStrategy.java`: Logic hung hăng

**ViewLogic (Vẽ lên màn hình):**

- `GraphicalView.java`: View đồ họa
- `BasicView.java`: View cơ bản
- `AdvancedRenderer.java`: Render với sprite
- `SimulationController.java`: Điều khiển hiển thị

### 4. Survival Strategy (Chiến Lược Sinh Tồn)

**PassiveStrategy:**

- Chỉ đi lang thang và ăn cỏ
- Dùng cho: Herbivore (Thỏ, Hươu, Voi, Vịt)

**HunterStrategy:**

- Luôn quét tìm mục tiêu trong bán kính 4.0 mét
- Tấn công khi vào phạm vi 2.0 mét
- Tăng tốc 1.5x khi đuổi theo
- Dùng cho: Predator (Sói, Hổ, Cá sấu, Người)

**ScaredStrategy:**

- Luôn di chuyển ngược hướng với kẻ thù gần nhất
- Chạy trốn khi phát hiện predator
- Dùng cho: Herbivore khi gặp nguy hiểm

**AggressiveStrategy:**

- Đánh trả khi bị đe dọa
- Có thể dùng khi động vật đói quá mức (tương lai)

## Cấu Trúc Project

```
OOP_Project/
├── src/
│   └── ecosystem/
│       ├── Main.java                    # Entry point
│       ├── behavior/                    # Hành vi AI
│       │   ├── HunterStrategy.java      # Strategy săn mồi
│       │   ├── PassiveStrategy.java     # Strategy thụ động
│       │   ├── ScaredStrategy.java      # Strategy sợ hãi
│       │   └── AggressiveStrategy.java  # Strategy hung hăng
│       ├── entities/                    # Các thực thể
│       │   ├── Animal.java              # Base class cho động vật
│       │   ├── Wolf.java                # Sói
│       │   ├── Tiger.java               # Hổ
│       │   ├── Crocodile.java           # Cá sấu
│       │   ├── Human.java               # Người
│       │   ├── Rabbit.java              # Thỏ
│       │   ├── Deer.java                # Hươu
│       │   ├── Duck.java                # Vịt
│       │   ├── Elephant.java            # Voi
│       │   ├── Fish.java                # Cá
│       │   └── Plant.java               # Thực vật
│       ├── environment/                 # Môi trường
│       │   ├── Environment.java          # Quản lý môi trường
│       │   ├── SeasonManager.java       # Quản lý mùa
│       │   ├── FoodFinder.java          # Tìm thức ăn
│       │   └── Grid.java                # Lưới bản đồ
│       ├── physics/                     # Vật lý
│       │   ├── Vector2D.java            # Vector 2D
│       │   └── ICollidable.java         # Interface va chạm
│       ├── terrain/                     # Địa hình
│       │   ├── Grid.java                # Lưới
│       │   ├── Tile.java                # Ô
│       │   └── TerrainType.java         # Loại địa hình
│       └── view/                        # Giao diện
│           ├── GraphicalView.java       # View đồ họa
│           └── render/                  # Render
├── bin/                                 # Compiled files
└── resources/                           # Tài nguyên (hình ảnh, v.v.)
```

## Các Thay Đổi Chính Trong Session Này

### 1. Cải Thiện Cơ Chế Săn Mồi

- Tăng phạm vi săn từ 1.0 → 2.0 ô
- Săn thành công ngay lập tức (không cần đợi timer)
- Tăng tốc độ đuổi theo (1.5x)
- Giảm fail chance từ 10% → 5%
- Xóa damage khi săn thất bại

### 2. Bảo Vệ Predator

- Tăng health recovery khi ăn: 1 → 3
- Giảm hungerRate predator: 2 → 1
- Giảm reproduction cooldown predator
- Tăng giới hạn quần thể predator
- Giảm giới hạn quần thể prey

### 3. Mở Rộng Chuỗi Thức Ăn

- Predator (Wolf, Tiger, Crocodile, Human) có thể ăn Duck
- Duck có thể ăn Grass và Fruit Tree

### 4. Tối Ưu Code

- Xóa biến không dùng (huntingTimer, currentPrey)
- Thêm comment tiếng Việt
- Đơn giản hóa logic heal

### 5. Sửa Lỗi

- HunterStrategy cho Human (thay vì PassiveStrategy)
- Predator không ăn plant khi không có prey
- Crocodile tăng tốc để đuổi kịp Duck trong nước

## Ghi Chú Kỹ Thuật

### HunterStrategy

- **Phạm vi nhìn thấy:** 4.0 ô
- **Phạm vi săn:** 2.0 ô
- **Tăng tốc:** 1.5x (trên đất), giữ nguyên (trong nước)
- **Fail chance:** 5%
- **Bảo tồn:** Không săn nếu prey < 3

### Animal

- **Health:** 100
- **Attack Damage:** 100 (predator)
- **Hunger Rate:** 1 (predator), 3 (herbivore mặc định)
- **Eat Timer:** 3 ticks (1.5 giây)
- **Drink Timer:** 2 ticks (1 giây)

## Tác Giả

- Project OOP - Hệ sinh thái hoang dã
- Năm: 2025

## Giấy Phép

- Project học tập - Mục đích giáo dục
