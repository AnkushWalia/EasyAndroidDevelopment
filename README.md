# EasyAndroidDevelopment
Now no need of project start with beginning. Here some stuff of daily need of code so approx 30% project are completion prevently. 

---------------------------------------------------------------------------------------------------------------------------
 
           ImageUtils.with(MainActivity.this, getString(R.string.app_name), new ImageUtils.ImageSelectCallback() {

                    @Override
                    public void onImageSelected(ArrayList<Image> imageData) {
                        
                    }
                }).setToolbarColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary))
		.show();

            }

        


----------------------------------------------------------------------------------------------------------------------------



                 @Override
                 public void onActivityResult(int requestCode, int resultCode, Intent data) {
                  ImageUtils.onActivityResult(requestCode, resultCode, data);
                 }



----------------------------------------------------------------------------------------------------------------------------




Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}


Step 2. Add the dependency

	dependencies {
	        compile 'com.github.AnkushWalia:EasyAndroidDevelopment:imagepickcrop-1.0.3'
	}


