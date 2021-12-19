CREATE TABLE landmarks
(
    id                   BIGSERIAL PRIMARY KEY,
    name                 TEXT             NOT NULL,                                     --название достопримечательности
    city                 TEXT             NOT NULL,                                     --город в котором находится
    image                TEXT             NOT NULL,                                     --картинка
    landmark_address     TEXT             NOT NULL,                                     --адрес
    undergrounds         TEXT             NOT NULL,                                     --ближайшее метро
    landmark_description TEXT             NOT NULL,                                     --описание
    removed              BOOLEAN          NOT NULL DEFAULT FALSE,                       --удаление
    landmark_web_site    TEXT             NOT NULL DEFAULT 'Сайт отсутствует',          --сайт
    current_time_now     timestamptz      NOT NULL DEFAULT CURRENT_TIMESTAMP,           --проверка текущего времени
    landmark_phone       TEXT             NOT NULL DEFAULT 'Нет возможности позвонить', --номер телефона
    lat                  DOUBLE PRECISION NOT NULL CHECK (lat > -90 and lat <= 90),
    lon                  DOUBLE PRECISION NOT NULL CHECK (lon > -180 and lon <= 180),
    open                 time with time zone,
    close                time with time zone
);

CREATE TABLE rateUp
(
  id BIGSERIAL PRIMARY KEY,
  name TEXT NOT NULL,
  city TEXT NOT NULL,
  image TEXT NOT NULL,
  landmark_address TEXT NOT NULL,
  rate DOUBLE PRECISION NOT NULL CHECK ( rate >= 0 and rate <=5 )
)