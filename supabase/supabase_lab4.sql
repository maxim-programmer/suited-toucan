alter table public.cars add column if not exists year integer;
alter table public.cars add column if not exists address text;
alter table public.cars add column if not exists description text;
alter table public.cars add column if not exists insurance_per_day integer not null default 300;
alter table public.cars add column if not exists deposit integer not null default 15000;

update public.cars set
    year = 2022,
    address = 'Авиамоторная ул., 8, стр. 2',
    description = 'Mercedes-Benz S 500 Sedan: бизнес-седан с мягкой подвеской, кожаным салоном и климат-контролем. Автоматическая коробка передач, комфорт для долгих поездок.'
where brand = 'Mercedes-Benz' and model = 'S 500 Sedan';

update public.cars set
    year = 2021,
    address = 'Ленинградский пр-т, 39, стр. 14',
    description = 'BMW 520d: экономичный дизельный седан с отличной динамикой и просторным салоном.'
where brand = 'BMW' and model = '520d';

update public.cars set
    year = 2022,
    address = 'Кутузовский пр-т, 12, стр. 3',
    description = 'Audi A6: комфортный седан с полным приводом, адаптивным круиз-контролем и мультимедиа последнего поколения.'
where brand = 'Audi' and model = 'A6';

update public.cars set
    year = 2020,
    address = 'Каширское ш., 31, стр. 1',
    description = 'Toyota Camry: надёжный семейный седан с просторным салоном и мягким ходом.'
where brand = 'Toyota' and model = 'Camry';

update public.cars set
    year = 2019,
    address = 'Профсоюзная ул., 56',
    description = 'Kia Rio: компактный и экономичный автомобиль для города.'
where brand = 'Kia' and model = 'Rio';

update public.cars set
    year = 2019,
    address = 'Варшавское ш., 125, стр. 5',
    description = 'Hyundai Solaris: простой в управлении городской седан с низким расходом топлива.'
where brand = 'Hyundai' and model = 'Solaris';

update public.cars set
    year = 2020,
    address = 'Волгоградский пр-т, 42, корп. 5',
    description = 'Volkswagen Polo: практичный и манёвренный седан с надёжной механической коробкой передач.'
where brand = 'Volkswagen' and model = 'Polo';

update public.cars set
    year = 2019,
    address = 'Авиамоторная ул., 8, стр. 2',
    description = 'Tesla Model 3 оснащена электрическим двигателем, который обеспечивает 460 лошадиных сил. Полный привод.',
    deposit = 15000
where brand = 'Tesla' and model = 'Model 3';

update public.cars set year = coalesce(year, 2021) where year is null;
update public.cars set address = coalesce(address, 'Москва, ул. Тверская, 1') where address is null;
update public.cars set description = coalesce(description, '') where description is null;
