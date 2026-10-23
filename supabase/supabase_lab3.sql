create table if not exists public.cars (
    id uuid primary key default gen_random_uuid(),
    brand text not null,
    model text not null,
    price_per_day integer not null check (price_per_day > 0),
    transmission text not null check (transmission in ('automatic', 'manual')),
    fuel_type text not null check (fuel_type in ('petrol', 'diesel', 'electric', 'hybrid')),
    image_path text,
    created_at timestamptz not null default now(),
    unique (brand, model)
);

alter table public.cars enable row level security;

drop policy if exists "cars_select_authenticated" on public.cars;
create policy "cars_select_authenticated" on public.cars
    for select to authenticated
    using (true);

insert into storage.buckets (id, name, public)
values ('car-images', 'car-images', true)
on conflict (id) do nothing;

insert into public.cars (brand, model, price_per_day, transmission, fuel_type) values
    ('Mercedes-Benz', 'S 500 Sedan', 2500, 'automatic', 'petrol'),
    ('BMW', '520d', 2100, 'automatic', 'diesel'),
    ('Audi', 'A6', 2300, 'automatic', 'petrol'),
    ('Toyota', 'Camry', 1700, 'automatic', 'petrol'),
    ('Kia', 'Rio', 1100, 'manual', 'petrol'),
    ('Hyundai', 'Solaris', 1000, 'manual', 'petrol'),
    ('Volkswagen', 'Polo', 1200, 'manual', 'petrol'),
    ('Tesla', 'Model 3', 3000, 'automatic', 'electric')
on conflict (brand, model) do nothing;
