import json
import requests
from PIL import Image
from io import BytesIO
import firebase_admin
from firebase_admin import credentials, firestore, storage

# Initialize Firebase Firestore
cred = credentials.Certificate('/home/spectra/AndroidStudioProjects/feedme-android/scraping/update_database/firebase_credentials.json')

firebase_admin.initialize_app(cred, {
    'storageBucket': 'feedme-33341.appspot.com'
})
db = firestore.client()
bucket = storage.bucket()


# Function to fetch the next highest-rated recipe with an empty image URL
def fetch_next_recipe_with_empty_image_url():
    recipes_ref = db.collection("recipesFinal")
    recipes_query = recipes_ref.where("imageUrl", "==", "").order_by("rating", direction=firestore.Query.DESCENDING).limit(1)
    recipes = [doc for doc in recipes_query.stream()]
    return recipes[0] if recipes else None

# Function to fetch image from URL
def fetch_image(url):
    headers = {
        "User-Agent": "Mozilla/5.0"
    }
    try:
        response = requests.get(url, headers=headers)
        response.raise_for_status()
        return response.content
    except requests.RequestException as e:
        print(f"Failed to fetch image from {url}: {e}")
        return None

# Function to crop and resize image to the correct aspect ratio (16:9)
def crop_and_resize_image(image_content):
    try:
        image = Image.open(BytesIO(image_content))
        width, height = image.size

        # Desired aspect ratio: 16:9
        desired_aspect_ratio = 16 / 9

        if width / height > desired_aspect_ratio:
            # Crop width
            new_width = int(height * desired_aspect_ratio)
            left = (width - new_width) / 2
            top = 0
            right = (width + new_width) / 2
            bottom = height
        else:
            # Crop height
            new_height = int(width / desired_aspect_ratio)
            left = 0
            top = (height - new_height) / 2
            right = width
            bottom = (height + new_height) / 2

        cropped_image = image.crop((left, top, right, bottom))
        resized_image = cropped_image.resize((1600, 900), Image.ANTIALIAS)  # Resize to 1600x900 for consistent dimensions
        return resized_image
    except Exception as e:
        print(f"Error cropping and resizing image: {e}")
        return None

# Function to upload image to Firestore Storage and get the URL
def upload_image_to_firestore(image, filename):
    blob = bucket.blob(f'recipes/{filename}')
    with BytesIO() as output:
        image.save(output, format='JPEG')
        blob.upload_from_string(output.getvalue(), content_type='image/jpeg')
    
    # Make the blob publicly viewable
    blob.make_public()
    
    return blob.public_url

# Function to update recipe in Firestore
def update_recipe_in_firestore(recipe_id, updated_recipe):
    db.collection("recipesFinal").document(recipe_id).set(updated_recipe, merge=True)

# Main function to run the interactive script
def main():
    while True:
        recipe_doc = fetch_next_recipe_with_empty_image_url()
        if not recipe_doc:
            print("No more recipes with empty image URLs.")
            break

        recipe = recipe_doc.to_dict()
        recipe_id = recipe_doc.id
        print(f"\nProcessing Recipe - {recipe['title']} (Rating: {recipe['rating']})")

        while True:
            image_url = input("Please input the image URL: ")

            if not image_url:
                print("No URL entered. Please enter a valid URL.")
                continue

            image_content = fetch_image(image_url)
            if image_content:
                cropped_resized_image = crop_and_resize_image(image_content)
                if cropped_resized_image:
                    filename = f"{recipe_id}.jpg"
                    new_image_url = upload_image_to_firestore(cropped_resized_image, filename)
                    recipe['imageUrl'] = new_image_url
                    update_recipe_in_firestore(recipe_id, recipe)
                    print(f"Recipe {recipe_id} updated with new image URL: {new_image_url}")
                    break
                else:
                    print("Failed to crop and resize image. Please try again.")
            else:
                print("Failed to fetch image. Please try again.")
        
        # Fetch the next recipe after processing the current one

    print("All recipes processed.")

if __name__ == "__main__":
    main()