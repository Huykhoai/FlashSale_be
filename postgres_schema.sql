-- Người dùng (tương đương miaosha_user)
CREATE TABLE fs_user (
                         id          BIGSERIAL PRIMARY KEY,
                         username    VARCHAR(50)  UNIQUE NOT NULL,
                         password    VARCHAR(64)  NOT NULL,          -- BCrypt
                         salt        VARCHAR(10),
                         register_date TIMESTAMPTZ DEFAULT NOW()
);

-- Sản phẩm (tương đương goods)
CREATE TABLE fs_goods (
                          id          BIGSERIAL PRIMARY KEY,
                          goods_name  VARCHAR(200) NOT NULL,
                          goods_img   VARCHAR(500),
                          goods_price NUMERIC(10,2) NOT NULL
);

-- Sự kiện Flash Sale (tương đương miaosha_goods)
CREATE TABLE fs_flash_event (
                                id          BIGSERIAL PRIMARY KEY,
                                goods_id    BIGINT        NOT NULL REFERENCES fs_goods(id),
                                flash_price NUMERIC(10,2) NOT NULL,
                                stock_count INT           NOT NULL CHECK (stock_count >= 0),
                                start_time  TIMESTAMPTZ   NOT NULL,
                                end_time    TIMESTAMPTZ   NOT NULL
);

-- Đơn hàng tổng (tương đương order_info)
CREATE TABLE fs_order (
                          id          BIGSERIAL PRIMARY KEY,
                          user_id     BIGINT        NOT NULL,
                          goods_id    BIGINT        NOT NULL,
                          goods_name  VARCHAR(200),
                          goods_price NUMERIC(10,2),
                          status      SMALLINT      DEFAULT 0,    -- 0: pending, 1: paid, 2: cancelled
                          create_time TIMESTAMPTZ   DEFAULT NOW()
);

-- Đơn hàng flash sale (tương đương miaosha_order)
-- Đây là bảng THEN CHỐT — chứa UNIQUE KEY chống mua 2 lần
CREATE TABLE fs_flash_order (
                                id          BIGSERIAL PRIMARY KEY,
                                user_id     BIGINT NOT NULL,
                                order_id    BIGINT NOT NULL REFERENCES fs_order(id),
                                goods_id    BIGINT NOT NULL,
                                CONSTRAINT uq_user_goods UNIQUE (user_id, goods_id)  -- ← Dây an toàn DB!
);
