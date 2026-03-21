import os
env_status = os.getenv('APP_ENV', 'not set')

print("---------------------------------")
print(f"Giá trị của APP_ENV là: {env_status}")
print("---------------------------------")