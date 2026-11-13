drop policy if exists "cars_delete_own" on public.cars;
create policy "cars_delete_own" on public.cars
    for delete to authenticated
    using (owner_id = auth.uid());

drop policy if exists "car_images_delete_own" on storage.objects;
create policy "car_images_delete_own" on storage.objects
    for delete to authenticated
    using (bucket_id = 'car-images' and (storage.foldername(name))[1] = auth.uid()::text);