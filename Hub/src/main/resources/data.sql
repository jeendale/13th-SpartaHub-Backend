INSERT INTO p_hub ("hub_id", "hubname", "address", "lati", "longti", "is_center_hub", "is_deleted", "created_at", "center_hub_id")
VALUES
    (gen_random_uuid(), '경기 남부 센터', '경기도 이천시 덕평로 257-21', 37.190440, 127.376068, true, false, now(),null),
    (gen_random_uuid(), '대전광역시 센터', '대전 서구 둔산로 100', 36.350382, 127.384835, true, false, now(),null),
    (gen_random_uuid(), '대구광역시 센터', '대구 북구 태평로 161', 35.875900, 128.595960, true, false, now(),null);

INSERT INTO p_hub ("hub_id", "hubname", "address", "lati", "longti", "is_center_hub", "is_deleted", "created_at", "center_hub_id")
VALUES
    (gen_random_uuid(), '서울특별시 센터', '서울특별시 송파구 송파대로 55', 37.479916, 127.121767, false, false, now(), (SELECT hub_id FROM p_hub WHERE hubname = '경기 남부 센터' LIMIT 1)),
    (gen_random_uuid(), '경기 북부 센터', '경기도 고양시 덕양구 권율대로 570', 37.479916, 126.873782, false, false, now(), (SELECT hub_id FROM p_hub WHERE hubname = '경기 남부 센터' LIMIT 1)),
    (gen_random_uuid(), '부산광역시 센터', '부산 동구 중앙대로 206', 35.115149, 129.042462, false, false, now(), (SELECT hub_id FROM p_hub WHERE hubname = '대구광역시 센터' LIMIT 1)),
    (gen_random_uuid(), '인천광역시 센터', '인천 남동구 정각로 29', 37.456037, 126.706215, false, false, now(), (SELECT hub_id FROM p_hub WHERE hubname = '경기 남부 센터' LIMIT 1)),
    (gen_random_uuid(), '광주광역시 센터', '광주 서구 내방로 111', 35.159953, 126.851299, false, false, now(), (SELECT hub_id FROM p_hub WHERE hubname = '대전광역시 센터' LIMIT 1)),
    (gen_random_uuid(), '울산광역시 센터', '울산 남구 중앙로 201', 35.538719, 129.311515, false, false, now(), (SELECT hub_id FROM p_hub WHERE hubname = '대구광역시 센터' LIMIT 1)),
    (gen_random_uuid(), '세종특별자치시 센터', '세종특별자치시 한누리대로 2130', 36.479895, 127.289091, false, false, now(), (SELECT hub_id FROM p_hub WHERE hubname = '대전광역시 센터' LIMIT 1)),
    (gen_random_uuid(), '강원특별자치도 센터', '강원특별자치도 춘천시 중앙로 1', 37.882520, 127.729143, false, false, now(), (SELECT hub_id FROM p_hub WHERE hubname = '경기 남부 센터' LIMIT 1)),
    (gen_random_uuid(), '충청북도 센터', '충북 청주시 상당구 상당로 82', 36.635361, 127.491486, false, false, now(), (SELECT hub_id FROM p_hub WHERE hubname = '대전광역시 센터' LIMIT 1)),
    (gen_random_uuid(), '충청남도 센터', '충남 홍성군 홍북읍 충남대로 21', 36.659066, 126.672915, false, false, now(), (SELECT hub_id FROM p_hub WHERE hubname = '대전광역시 센터' LIMIT 1)),
    (gen_random_uuid(), '전북특별자치도 센터', '전북특별자치도 전주시 완산구 효자로 225', 35.8200002, 127.109125, false, false, now(), (SELECT hub_id FROM p_hub WHERE hubname = '대전광역시 센터' LIMIT 1)),
    (gen_random_uuid(), '전라남도 센터', '전남 무안군 삼향읍 오룡길 1', 34.816165, 126.462913, false, false, now(), (SELECT hub_id FROM p_hub WHERE hubname = '대전광역시 센터' LIMIT 1)),
    (gen_random_uuid(), '경상북도 센터', '경북 안동시 풍천면 도청대로 455', 36.575344, 128.505818, false, false, now(), (SELECT hub_id FROM p_hub WHERE hubname = '대구광역시 센터' LIMIT 1)),
    (gen_random_uuid(), '경상남도 센터', '경남 창원시 의창구 중앙대로 300', 35.237344, 128.691550, false, false, now(), (SELECT hub_id FROM p_hub WHERE hubname = '대구광역시 센터' LIMIT 1));