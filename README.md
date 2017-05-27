This is an android app that lets you scan the barcode and qr code to get the data. You can copy the data. If it is a url then you can open the link in a browser or google search the product. You can save the data for further reference.

Add the library in build.gradle
> compile 'com.journeyapps:zxing-android-embedded:3.0.2@aar'
> compile 'com.google.zxing:core:3.2.0'

For scanning
>       IntentIntegrator intentIntegrator = new IntentIntegrator(ScanActivity.this);
>       intentIntegrator.setBeepEnabled(true);
>       intentIntegrator.setOrientationLocked(false);
>       intentIntegrator.setPrompt("Scan the barcode or QR code to get the data!");
>       intentIntegrator.initiateScan();

Getting the results
>   @Override
>   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
>       IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
>       if (result != null) {
>           if (result.getContents() == null) {
>               Log.d("ScanActivity", "Cancelled scan");
>           } else {
>               scanContent = result.getContents();
>               Log.d("ScanActivity", "Scanned");
>           }
>       } else
>           super.onActivityResult(requestCode, resultCode, data);
>   }

Generating a QR code
>                   QRCodeWriter writer = new QRCodeWriter();
                    try {
                        BitMatrix bitMatrix = writer.encode("Message", BarcodeFormat.QR_CODE, 512, 512);
                        int width = bitMatrix.getWidth();
                        int height = bitMatrix.getHeight();
                        bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                        for (int x = 0; x < width; x++) {
                            for (int y = 0; y < height; y++) {
                                bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                            }
                        }
                        iv.setImageBitmap(bmp);
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
