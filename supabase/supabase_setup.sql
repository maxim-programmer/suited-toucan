create table if not exists public.profiles (
    id uuid primary key references auth.users (id) on delete cascade,
    email text not null,
    last_name text not null,
    first_name text not null,
    middle_name text,
    birth_date date not null,
    gender text not null check (gender in ('male', 'female')),
    license_number text not null check (char_length(license_number) = 10),
    license_issue_date date not null,
    avatar_path text,
    license_photo_path text,
    passport_photo_path text,
    created_at timestamptz not null default now()
);

alter table public.profiles enable row level security;

create policy "profiles_select_own" on public.profiles
    for select to authenticated
    using (auth.uid() = id);

create policy "profiles_insert_own" on public.profiles
    for insert to authenticated
    with check (auth.uid() = id);

create policy "profiles_update_own" on public.profiles
    for update to authenticated
    using (auth.uid() = id)
    with check (auth.uid() = id);

insert into storage.buckets (id, name, public)
values ('user-documents', 'user-documents', false)
on conflict (id) do nothing;

create policy "documents_select_own" on storage.objects
    for select to authenticated
    using (bucket_id = 'user-documents' and (storage.foldername(name))[1] = auth.uid()::text);

create policy "documents_insert_own" on storage.objects
    for insert to authenticated
    with check (bucket_id = 'user-documents' and (storage.foldername(name))[1] = auth.uid()::text);

create policy "documents_update_own" on storage.objects
    for update to authenticated
    using (bucket_id = 'user-documents' and (storage.foldername(name))[1] = auth.uid()::text)
    with check (bucket_id = 'user-documents' and (storage.foldername(name))[1] = auth.uid()::text);
