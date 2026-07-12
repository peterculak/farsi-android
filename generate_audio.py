import os
import re
from urllib.parse import quote
import time

kt_file = "app/src/main/java/com/example/farsialphabet/Data.kt"
out_dir = "app/src/main/res/raw"

os.makedirs(out_dir, exist_ok=True)

with open(kt_file, "r", encoding="utf-8") as f:
    content = f.read()

pattern = r'FarsiLetter\((\d+),\s*"[^"]*",\s*"[^"]*",\s*"([^"]*)"'

for match in re.finditer(pattern, content):
    letter_id = match.group(1)
    full_form = match.group(2)
    
    encoded_text = quote(full_form)
    url = f"https://translate.google.com/translate_tts?ie=UTF-8&q={encoded_text}&tl=fa&client=tw-ob"
    out_path = f"{out_dir}/letter_{letter_id}.mp3"
    
    print(f"Downloading audio for letter {letter_id}: {full_form}")
    
    # Use curl to download it
    ret = os.system(f"curl -s -A 'Mozilla/5.0' '{url}' -o {out_path}")
    if ret != 0:
        print(f"Failed {letter_id}")
        
    time.sleep(0.5)

print("Done downloading Farsi audio files with curl.")
