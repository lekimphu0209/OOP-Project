# Đề tài 3: Hệ thống Mô phỏng Hệ sinh thái Hoang dã (Wild-Life Eco Simulation)

## Tổng quan

Đây là phiên bản tích hợp thống nhất từ 3 nhánh thành viên:

| Nhánh | Thành viên | Đóng góp |
|-------|-----------|----------|
| feature-Logic | - | Hệ thống vật lý, va chạm, di chuyển |
| leanhvu | Lê Anh Vũ | Mẫu State cho hành vi động vật |
| leviethoa | Lê Việt Hòa | Mẫu Strategy cho AI động vật |

---

## Cấu trúc Package

```
src/ecosystem/
├── Main.java                        # Entry point chính
│
├── physics/                         # Hệ thống vật lý (BioLogic)
│   ├── Vector2D.java               # Toán tử vector 2D (cộng, trừ, nhân, chuẩn hóa, khoảng cách, vuông góc)
│   ├── PhysicsBody.java            # Thuộc tính vật lý (vị trí, vận tốc, bán kính, khối lượng)
│   ├── CollisionDetector.java      # Phát hiện va chạm hình tròn
│   ├── MovementEngine.java         # Engine di chuyển với yielding & terrain modifier
│   ├── ICollidable.java            # Interface đối tượng có thể va chạm
│   ├── IMovable.java               # Interface đối tượng có thể di chuyển
│   └── IYieldable.java             # Interface cơ chế nhường đường
│
├── behavior/                        # Hệ thống hành vi (BioLogic)
│   ├── State.java                  # Interface trạng thái (State Pattern)
│   ├── SurvivalStrategy.java       # Interface chiến lược sinh tồn (Strategy Pattern)
│   ├── WanderingState.java         # Trạng thái đi lang thang
│   ├── HungryState.java            # Trạng thái đói - tìm thức ăn
│   ├── ThirstyState.java           # Trạng thái khát - tìm nước
│   ├── PassiveStrategy.java        # Chiến lược thụ động - di chuyển ngẫu nhiên
│   ├── HunterStrategy.java         # Chiến lược săn mồi - đuổi & tấn công con mồi
│   ├── ScaredStrategy.java         # Chiến lược sợ hãi - chạy trốn & trốn vào rừng
│   └── AggressiveStrategy.java     # Chiến lược hung hăng - săn mồi mạnh hơn khi đói
│
├── entities/                        # Các thực thể (BioLogic)
│   ├── Entity.java                 # Lớp cơ sở trừu tượng (position, radius)
│   ├── Animal.java                 # Lớp động vật (State + Strategy + canSwim/canWalk/hungerRate/starvation)
│   ├── Plant.java                  # Lớp thực vật (sinh sôi, 10 giai đoạn phát triển, edible khi stage>=3)
│   ├── Rabbit.java                 # Thỏ - ăn cỏ, ScaredStrategy
│   ├── Deer.java                   # Hươu - ăn cỏ, ScaredStrategy
│   ├── Wolf.java                   # Sói - ăn thịt, HunterStrategy, hungerRate=2
│   ├── Tiger.java                  # Hổ - ăn thịt, HunterStrategy, hungerRate=2
│   ├── Elephant.java               # Voi - ăn cỏ, PassiveStrategy, không nhường đường
│   ├── Human.java                  # Người - đặc biệt, PassiveStrategy, không nhường đường
│   ├── Fish.java                   # Cá - chỉ bơi (canSwim=true, canWalk=false)
│   ├── Duck.java                   # Vịt - vừa bơi vừa đi (canSwim=true, canWalk=true)
│   └── Crocodile.java             # Cá sấu - ăn thịt, bơi+đi, chỉ đi xa nước tối đa 3 ô, nhanh dưới nước
│
├── environment/                     # Môi trường (BioLogic)
│   └── Environment.java            # Quản lý môi trường, danh sách thực thể, hệ thống mùa, findNearestPrey
│
├── terrain/                         # Địa hình (BioLogic)
│   ├── TerrainType.java            # Enum 5 loại địa hình (Cỏ, Rừng, Nước, Bùn, Vật cản)
│   ├── Tile.java                   # Ô địa hình (có thể thay đổi loại runtime qua setType)
│   └── Grid.java                   # Lưới bản đồ với tạo map ngẫu nhiên (50% Cỏ, 25% Rừng, 15% Nước, 10% Vật cản)
│
├── view/                            # Giao diện (ViewLogic)
│   ├── BasicView.java              # GUI Basic mode - hình tròn/vuông + zoom/pan + điều khiển thủ công
│   └── GraphicalView.java          # GUI Graphical mode - sprite sheet + nút chuyển đổi Basic/Graphical
│
└── controller/                      # Điều khiển (ViewLogic)
    └── SimulationController.java    # Controller chính - vòng lặp simulation, spawn có trọng số, 9 loại động vật
```

---

## Chi tiết các tính năng

### 1. Hệ thống Địa hình (5 loại)

| Địa hình | Màu GUI | Có thể đi qua | Hệ số tốc độ | Đặc điểm |
|----------|---------|---------------|--------------|----------|
| Cỏ (Grass) | Xanh lá `#228B22` | ✅ | 1.0x | Đất trống, tốc độ bình thường |
| Rừng (Forest) | Xanh đậm `#006400` | ✅ | 0.7x | Động vật ăn cỏ trốn được, sói không vào |
| Nước (Water) | Xanh dương `#1E90FF` | ❌ (chỉ canSwim) | 1.0x | Chỉ Fish/Duck/Crocodile đi qua được |
| Bùn (Mud) | Nâu `#8B4513` | ✅ | 0.4x | Đi rất chậm |
| Vật cản (Obstacle) | Xám `#808080` | ❌ | 0.0x | Chặn đường đi, có thể đặt thủ công |

### 2. Động vật (9 loại)

#### Động vật trên cạn

| Loài | Hình dạng | Màu GUI | Máu | Tốc độ | Ưu tiên | Ăn thịt? | hungerRate | Chiến lược | Đặc điểm |
|------|----------|---------|-----|--------|---------|----------|-----------|-----------|----------|
| Thỏ | Tròn | Trắng | 30 | 1.2 | 1 | ❌ | 1 | ScaredStrategy | Chạy trốn vào rừng |
| Hươu | Tròn | Nâu `#8B4513` | 50 | 1.0 | 2 | ❌ | 1 | ScaredStrategy | Chạy trốn vào rừng |
| Sói | Vuông | Xám | 50 | 1.5 | 3 | ✅ | 2 | HunterStrategy | Tăng tốc 1.8x khi săn |
| Hổ | Vuông | Cam | 70 | 1.3 | 4 | ✅ | 2 | HunterStrategy | Tăng tốc 1.8x khi săn |
| Voi | Tròn | Xám nhạt `#A9A9A9` | 150 | 0.8 | 5 | ❌ | 1 | PassiveStrategy | Không nhường ai |
| Người | Tròn | Xanh | 100 | 1.0 | 5 | ✅ | 1 | PassiveStrategy | Không nhường ai |

#### Động vật dưới nước / lưỡng cư

| Loài | Hình dạng | Màu GUI | Máu | Tốc độ | Ưu tiên | Ăn thịt? | canSwim | canWalk | Chiến lược | Đặc điểm |
|------|----------|---------|-----|--------|---------|----------|---------|---------|-----------|----------|
| Cá | Tròn | Cyan `#00FFFF` | 20 | 0.8 | 0 | ❌ | ✅ | ❌ | PassiveStrategy | Chỉ sống trong nước |
| Vịt | Tròn | Vàng | 25 | 1.0 | 0 | ❌ | ✅ | ✅ | PassiveStrategy | Bơi & đi trên cạn |
| Cá sấu | Vuông | Xanh đậm `#006400` | 50 | 0.7 | 3 | ✅ | ✅ | ✅ | HunterStrategy | Nhanh dưới nước (1.2x), chậm trên cạn (0.6x), attackDamage=20, chỉ đi tối đa 3 ô khỏi nước |

### 3. Thực vật (2 loại)

| Loại | Kích thước GUI | Dinh dưỡng | Tỷ lệ xuất hiện |
|------|---------------|-----------|-----------------|
| Cỏ | Điểm xanh lá nhỏ (10px) | 5.0 | 70% |
| Cây ăn quả | Điểm cam lớn (16px) | 10.0 | 30% |

- Tự động sinh sôi vào **Mùa Xuân** (10% mỗi tick)
- **10 giai đoạn phát triển** (0→10), chỉ ăn được khi `growthStage >= 3`
- Dinh dưỡng tỷ lệ với giai đoạn: `nutrition * (stage / maxStage)`
- Khi bị ăn: reset về `growthStage = 0` (mọc lại)

### 4. Hệ thống Sinh tồn (Starvation)

```
Animal.act() mỗi tick:
  1. hunger += hungerRate * seasonFactor    (Winter: 1.5x hunger)
  2. thirst++
  3. Nếu hunger > threshold HOẶC thirst > 50:
     → takeDamage(baseDamage * seasonFactor)
     - Predator: threshold=40, baseDamage=10
     - Prey:     threshold=50, baseDamage=2
     - Winter:   damage x1.5
  4. state.handle() → kiểm tra & chuyển trạng thái
  5. strategy.execute() → quyết định di chuyển
  6. move() → thực hiện di chuyển (kiểm tra canSwim/canWalk)
```

### 5. Hệ thống Di chuyển (canSwim / canWalk)

```java
// Animal.move() kiểm tra:
if (đích đến là Nước) {
    if (canSwim) → được đi
    else → dừng lại
} else {
    if (canWalk && isWalkable) → được đi
    else → dừng lại
}
```

| Loài | canSwim | canWalk | Hành vi |
|------|---------|---------|---------|
| Thỏ, Hươu, Sói, Hổ, Voi, Người | ❌ | ✅ | Chỉ đi trên cạn, dừng trước nước |
| Cá | ✅ | ❌ | Chỉ bơi trong nước |
| Vịt | ✅ | ✅ | Vừa bơi vừa đi |
| Cá sấu | ✅ | ✅ | Bơi nhanh (1.2x), đi chậm (0.6x), không đi xa >3 ô khỏi nước |

### 6. Hệ thống Mùa (4 mùa)

| Mùa | Hệ số sinh sản | Mô tả | Ảnh hưởng khác |
|-----|----------------|-------|---------------|
| Mùa Xuân (Spring) | 1.2x | Sinh sản nhiều | Thực vật sinh sôi (10%/tick) |
| Mùa Hạ (Summer) | 1.0x | Bình thường | - |
| Mùa Thu (Autumn) | 0.8x | Giảm dần | - |
| Mùa Đông (Winter) | 0.5x | Ít sinh sản | Hunger x1.5, Starvation damage x1.5 |

- Mùa tự động chuyển đổi mỗi ~20 tick (≈10 giây)
- Chuyển theo vòng: Xuân → Hạ → Thu → Đông → Xuân...
- Hiển thị mùa + mô tả ở thanh thông tin GUI

### 7. Điều khiển Thủ công (Manual Control)

| Nút | Chức năng |
|-----|----------|
| **Trồng thức ăn** | Click trái vào ô Cỏ/Rừng để trồng cỏ hoặc cây ăn quả |
| **Đặt vật cản** | Click trái vào ô bất kỳ để đặt vật cản chặn đường |

### 8. Camera: Zoom & Pan

| Thao tác | Cách thực hiện |
|----------|---------------|
| **Zoom in/out** | Cuộn chuột (mouse wheel) - từ 0.2x đến 5.0x |
| **Pan (di chuyển camera)** | Click phải + kéo chuột |
| **Zoom về vị trí chuột** | Tự động điều chỉnh offset khi zoom |

- Khi zoom > 0.8x: hiển thị thanh máu trên mỗi con vật
- Khi zoom > 0.5x: hiển thị đường lưới ô đất
- Khi zoom nhỏ: ẩn chi tiết, xem toàn bản đồ

### 9. Tương tác Nâng cao

- **Thỏ/Hươu trốn trong rừng**: Tự động tìm rừng gần nhất khi bị đuổi
- **Sói/Hổ không vào rừng**: Nếu con mồi vào rừng, dừng đuổi → chuyển Passive
- **Tăng tốc khi trốn**: Động vật ăn cỏ tăng tốc 1.3-1.5x khi phát hiện kẻ thù
- **Tăng tốc khi săn**: Sói/Hổ tăng tốc 1.8x khi đuổi mồi
- **Cá sấu giới hạn nước**: Không đi xa >3 ô khỏi nước, tự động quay lại hồ
- **Cơ chế nhường đường**: Động vật priority thấp lách sang bên (sidestep)

### 10. Mẫu State Pattern (Hành vi nội tại)

| Trạng thái | Điều kiện chuyển vào | Hành vi |
|-----------|---------------------|---------|
| WanderingState | Mặc định | Đi lang thang, kiểm tra hunger/thirst |
| HungryState | hunger > 50 | Tìm thức ăn gần nhất, ăn khi đến nơi |
| ThirstyState | thirst > 50 | Tìm nước gần nhất, uống khi ở cạnh hồ |

### 11. Mẫu Strategy Pattern (Chiến lược sinh tồn)

| Chiến lược | Đối tượng sử dụng | Hành vi |
|-----------|-------------------|---------|
| PassiveStrategy | Voi, Người, Cá, Vịt | Di chuyển ngẫu nhiên, không tấn công |
| HunterStrategy | Sói, Hổ, Cá sấu | Tìm con mồi gần nhất, đuổi & tấn công |
| ScaredStrategy | Thỏ, Hươu | Chạy trốn kẻ thù, tìm rừng để trốn |
| AggressiveStrategy | Có thể gán động | Săn mồi hung hăng, bonus damage 1.5x |

### 12. Cơ chế Nhường đường (Yielding)

```
IYieldable.mustYieldTo(other):
  - return other.getPriority() > this.getPriority()

Ví dụ: Thỏ (1) nhường Sói (3) nhường Voi (5)
       Voi & Người (5) → KHÔNG nhường ai (override mustYieldTo = false)
       Cá & Vịt (0) → nhường tất cả
```

### 13. Spawn có trọng số (Weighted Spawn)

```
70% Prey:  Thỏ, Hươu, Voi, Cá, Vịt
20% Predator: Sói, Hổ, Cá sấu
10% Special: Người
```

- Fish/Duck/Crocodile chỉ spawn trên ô Nước
- Các loài cạn chỉ spawn trên ô đi được (không phải Nước)
- Thử tối đa 10 lần tìm vị trí hợp lệ

---

## Kiến trúc OOP

### Tách biệt BioLogic và ViewLogic

```
┌──────────────────────────────────────────────────┐
│                    ViewLogic                       │
│  ┌──────────────┐ ┌──────────────┐ ┌───────────┐ │
│  │  BasicView   │ │GraphicalView │ │Simulation │ │
│  │  (shapes)    │ │  (sprites)   │ │Controller │ │
│  │  zoom/pan    │ │  toggle btn  │ │  (timer)  │ │
│  └──────┬───────┘ └──────┬───────┘ └─────┬─────┘ │
└─────────┼────────────────┼───────────────┼────────┘
          │                │               │
          └────────────────┼───────────────┘
                           ▼
┌──────────────────────────────────────────────────┐
│                    BioLogic                        │
│  ┌────────────┐ ┌──────────┐ ┌─────────────────┐ │
│  │  entities  │ │ behavior │ │    physics       │ │
│  │ Animal     │ │ State    │ │ Vector2D         │ │
│  │ Plant      │ │ Strategy │ │ CollisionDetector│ │
│  │ 9 species  │ │ 4 strat  │ │ MovementEngine   │ │
│  ├────────────┤ ├──────────┤ ├─────────────────┤ │
│  │ environment│ │ terrain  │ │   interfaces     │ │
│  │ Season     │ │ Grid     │ │ ICollidable      │ │
│  │ starvation │ │ Tile     │ │ IMovable         │ │
│  │ findPrey   │ │ 5 types  │ │ IYieldable       │ │
│  └────────────┘ └──────────┘ └─────────────────┘ │
└──────────────────────────────────────────────────┘
```

### Tính mở rộng (Extensibility)

| Muốn thêm... | Cách làm |
|-------------|---------|
| Loài động vật mới | Kế thừa `Animal`, set `canSwim`/`canWalk`/`hungerRate`, chọn Strategy |
| Chiến lược mới | Implement `SurvivalStrategy` |
| Trạng thái mới | Implement `State` |
| Loại địa hình mới | Thêm vào enum `TerrainType` |
| Chế độ hiển thị mới | Kế thừa `BasicView` (như `GraphicalView`) |

### Design Patterns sử dụng

| Pattern | Vai trò | Ví dụ |
|---------|---------|-------|
| **State Pattern** | Quản lý trạng thái nội tại động vật | WanderingState → HungryState → ThirstyState |
| **Strategy Pattern** | Quản lý chiến lược sinh tồn | HunterStrategy, ScaredStrategy, PassiveStrategy |
| **Interface Segregation** | Tách biệt khả năng vật lý | ICollidable, IMovable, IYieldable |
| **Template Method** | Animal.act() định nghĩa flow, subclass override move() | Crocodile.move() thêm logic giới hạn nước |
| **MVC** | Tách biệt Model-View-Controller | Environment (Model), BasicView (View), SimulationController (Controller) |
| **Inheritance** | Phân cấp thực thể | Entity → Animal → 9 loài cụ thể |

---

## Cách chạy chương trình

### Yêu cầu
- Java 17+ (JDK)
- Hỗ trợ Swing GUI

### Biên dịch
```bash
cd d:\OOP\OOP_Project
javac -encoding UTF-8 -d bin src/ecosystem/*.java src/ecosystem/physics/*.java src/ecosystem/behavior/*.java src/ecosystem/entities/*.java src/ecosystem/environment/*.java src/ecosystem/terrain/*.java src/ecosystem/view/*.java src/ecosystem/view/render/*.java src/ecosystem/controller/*.java
```

### Chạy
```bash
java -cp bin ecosystem.Main
```

### Điều khiển GUI

| Thao tác | Cách thực hiện |
|----------|---------------|
| Trồng thức ăn | Chọn nút "Trồng thức ăn" → Click trái vào ô Cỏ/Rừng |
| Đặt vật cản | Chọn nút "Đặt vật cản" → Click trái vào ô bất kỳ |
| Zoom in/out | Cuộn chuột |
| Pan camera | Click phải + kéo chuột |
| Chuyển Basic/Graphical | Nút "Chuyển chế độ View" (chỉ GraphicalView) |

---

## Yêu cầu đề bài đã đáp ứng

- [x] Thế giới có vùng: Cỏ, Rừng, Nước, Bùn, Vật cản (+ bản đồ kết hợp)
- [x] Ít nhất 5 loại thực thể (có **9 động vật** + 2 thực vật)
- [x] Động vật có hunger, thirst, health, behavior
- [x] Movement affected by terrain speed modifiers
- [x] Animals yield to higher priority animals
- [x] Stop at obstacles
- [x] Drink water if thirsty
- [x] User controls: plant food, place obstacles
- [x] Two GUI modes: Basic (shapes) + Graphical (sprites, toggle)
- [x] Zoom & Pan camera (mouse wheel + right-click drag)
- [x] Extensible OOP design for adding new species and strategies
- [x] Seasonal system adjusting population density (4 mùa + winter penalty)
- [x] Separation of BioLogic and ViewLogic
- [x] State Pattern + Strategy Pattern combined
- [x] Thỏ lách qua bụi rậm trốn sói
- [x] Sói tăng tốc đuổi mồi
- [x] Cá sấu giới hạn gần nước, nhanh dưới nước
- [x] Cá chỉ sống trong nước, Vịt lưỡng cư
- [x] Starvation: mất máu khi đói/khát quá lâu (Winter x1.5)

## Tính năng có thể mở rộng thêm

1. **Hiệu ứng âm thanh**: Tiếng chim hót, tiếng gầm hổ, tiếng bước chân
2. **Lưu/Load**: Lưu trạng thái simulation ra file
3. **Statistic**: Thống kê số lượng từng loài theo thời gian
4. **Sinh sản động vật**: Hai con cùng loài gần nhau → sinh con mới
5. **MUD sinh sôi**: Bùn xuất hiện gần nước vào mùa mưa

---

## Tác giả tích hợp

Tích hợp từ 3 nhánh thành viên:
- **feature-Logic**: Physics & Collision system (Vector2D, PhysicsBody, CollisionDetector, MovementEngine, Interfaces)
- **leanhvu**: State Pattern (WanderingState, HungryState, ThirstyState, Animal, Deer, Rabbit, Environment)
- **leviethoa**: Strategy Pattern (HunterStrategy, PatrolStrategy, Animal, Wolf, Tiger, Elephant, Human, Rabbit)
