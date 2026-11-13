alter table public.cars drop constraint if exists cars_brand_model_key;

alter table public.cars add column if not exists year integer;
alter table public.cars add column if not exists address text;
alter table public.cars add column if not exists description text;
alter table public.cars add column if not exists mileage integer not null default 0;
alter table public.cars add column if not exists owner_id uuid references auth.users (id) on delete set null;
alter table public.cars add column if not exists photo_paths text[] not null default '{}';

alter table public.cars alter column price_per_day set default 1500;
alter table public.cars alter column fuel_type set default 'petrol';

drop policy if exists "cars_insert_own" on public.cars;
create policy "cars_insert_own" on public.cars
    for insert to authenticated
    with check (owner_id = auth.uid());

drop policy if exists "cars_update_own" on public.cars;
create policy "cars_update_own" on public.cars
    for update to authenticated
    using (owner_id = auth.uid())
    with check (owner_id = auth.uid());

drop policy if exists "car_images_select_own" on storage.objects;
create policy "car_images_select_own" on storage.objects
    for select to authenticated
    using (bucket_id = 'car-images' and (storage.foldername(name))[1] = auth.uid()::text);

drop policy if exists "car_images_insert_own" on storage.objects;
create policy "car_images_insert_own" on storage.objects
    for insert to authenticated
    with check (bucket_id = 'car-images' and (storage.foldername(name))[1] = auth.uid()::text);

drop policy if exists "car_images_update_own" on storage.objects;
create policy "car_images_update_own" on storage.objects
    for update to authenticated
    using (bucket_id = 'car-images' and (storage.foldername(name))[1] = auth.uid()::text)
    with check (bucket_id = 'car-images' and (storage.foldername(name))[1] = auth.uid()::text);