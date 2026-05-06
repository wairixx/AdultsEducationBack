import os

# Вкажи шлях до src/main відносно місця, де лежить скрипт
root_dir = 'src/main'
output_file = 'all_code.txt'

with open(output_file, 'w', encoding='utf-8') as outfile:
    for foldername, subfolders, filenames in os.walk(root_dir):
        for filename in filenames:
            # Можна додати фільтр, якщо треба тільки певні типи файлів:
            # if filename.endswith(('.java', '.properties', '.yaml', '.yml', '.sql')):
            file_path = os.path.join(foldername, filename)
            outfile.write(f'// --- {file_path} ---\n')
            try:
                with open(file_path, 'r', encoding='utf-8') as infile:
                    outfile.write(infile.read())
            except Exception as e:
                outfile.write(f'// Не вдалося прочитати файл: {e}\n')
            outfile.write('\n\n')
print(f'Весь код і конфігурації скопійовано у {output_file}')