package com.jean.productauthentifier;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.jean.productauthentifier.api.APIClient;
import com.jean.productauthentifier.api.UserApiService;
import com.jean.productauthentifier.common.CommonProvider;
import com.jean.productauthentifier.model.ProductAuthentifier;

import java.io.IOException;
import java.io.InputStream;

public class HomeActivity extends AppCompatActivity {

    private ImageView menuIcon;
    private TextView usernameTextV;
    private String username;
    private boolean isLoggedIn;
    private Button btnQrCamera, btnQrMemory;
    private ProgressBar loadingPB;
    private ActivityResultLauncher<String> galleryLauncher;

    private static final int PERMISSION_REQUEST_CAMERA = 1;
    public static final int PERMISSION_REQUEST_PHOTO_PICKER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        username = getIntent().getStringExtra("username");
        isLoggedIn = getIntent().getBooleanExtra("isLoggedIn", false);

        if (!isLoggedIn) {
            redirectToLogin();
            return;
        }

        componentsInitializer();

        usernameTextV.setText(username);

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });

        btnQrCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QRCodeScannerTrigger();
            }
        });

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                result -> handleGalleryResult(result));

        checkPermissions();

    }

    private void componentsInitializer() {
        menuIcon = (ImageView) findViewById(R.id.menuIconView);
        usernameTextV = (TextView) findViewById(R.id.usernameTextView);
        btnQrCamera = (Button) findViewById(R.id.btnQrCamera);
        btnQrMemory = (Button) findViewById(R.id.btnQrMemory);
        loadingPB = (ProgressBar) findViewById(R.id.idLoadingPB);
    }

    private void QRCodeScannerTrigger() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
            } else {
                initQRCodeScanner();
            }
        } else {
            initQRCodeScanner();
        }
    }

    private void initQRCodeScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setOrientationLocked(true);
        integrator.setPrompt("ProAuth - QR code Scanner");
        integrator.initiateScan();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initQRCodeScanner();
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_LONG).show();
                finish();
            }
        } else if (requestCode == PERMISSION_REQUEST_PHOTO_PICKER) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Gallery Permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Scan cancelled", Toast.LENGTH_LONG).show();
            } else {
                //Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                if (result.getContents().contains("P-")) {

                    String[] productDetails = result.getContents()
                            .replace("\n", "")
                            .split("P-");
                    int productId = Integer.valueOf(productDetails[1]);
                    String productReference = productDetails[2];
                    String productHash = productDetails[3];
                    if (CommonProvider.isNetworkAvailable(HomeActivity.this))
                        productAuthintifierApiCaller(productId, productReference, productHash, username);
                    else
                        CommonProvider.showAlert("Please Check your Internet Connectivity", HomeActivity.this);

                } else
                    Toast.makeText(this, "Alert: QR Code is not valid", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void checkPermissions() {
        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_PHOTO_PICKER);
        }
    }

    public void openGalleryOnClick(View view) {
        openGallery();
    }

    private void openGallery() {
        galleryLauncher.launch("image/*");
    }

    private void handleGalleryResult(Uri result) {
        if (result != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(result);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                //imageView.setImageBitmap(bitmap);

                Result qrCodeResult = scanQRCode(bitmap);
                if (qrCodeResult != null) {
                    if (qrCodeResult.getText().contains("P-")) {

                        String[] productDetails = qrCodeResult.getText().split("P-");
                        int productId = Integer.valueOf(productDetails[0].trim());
                        String productReference = productDetails[1].trim();
                        String productHash = productDetails[2].trim();
                        if (CommonProvider.isNetworkAvailable(HomeActivity.this))
                            productAuthintifierApiCaller(productId, productReference, productHash, username);
                        else
                            CommonProvider.showAlert("Please Check your Internet Connectivity", HomeActivity.this);

                    } else
                        Toast.makeText(this, "Alert: QR Code is not valid", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(this, "No QR code found in the image", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Result scanQRCode(Bitmap bitmap) {
        MultiFormatReader reader = new MultiFormatReader();
        try {
            int[] intArray = new int[bitmap.getWidth() * bitmap.getHeight()];
            bitmap.getPixels(intArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
            return reader.decode(new BinaryBitmap(new HybridBinarizer(
                    new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), intArray))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void productAuthintifierApiCaller(final int productId,
                                              final String productReference,
                                              final String productHash,
                                              final String requester) {

        // Displaying our progress bar.
        loadingPB.setVisibility(View.VISIBLE);

        final UserApiService userApiService = APIClient.getClient().create(UserApiService.class);

        final ProductAuthentifier productAuthentifier = new ProductAuthentifier(productId,
                productReference,
                productHash,
                requester);
        Call<ProductAuthentifier> call = userApiService.productAuthentifier(productAuthentifier);
        call.enqueue(new Callback<ProductAuthentifier>() {
            @Override
            public void onResponse(Call<ProductAuthentifier> call, Response<ProductAuthentifier> response) {
                loadingPB.setVisibility(View.GONE);

                if (response.code() == 200) {
                    String message = "";

                    if (response.body().getResult().equals("SUCCESS")) {
                        message = "Trusted: Product is certified";
                        CommonProvider.showAlertDialog(HomeActivity.this, "FOUND", productAuthMessageFormatter(response.body(), message), 0);
                    } else {
                        message = "Untrusted: Product is not certified";
                        CommonProvider.showAlertDialog(HomeActivity.this, "NOT FOUND", message, 1);
                    }
                    //CommonProvider.showAlert(productAuthMessageFormatter(response.body(), message),HomeActivity.this);
                } else {
                    try {
                        //CommonProvider.showAlert("Failed:" + response.errorBody().string(), HomeActivity.this);
                        CommonProvider.showAlertDialog(HomeActivity.this, "NOT FOUND", "Failed:" + response.errorBody().string(), 1);
                    } catch (IOException ex) {
                        Toast.makeText(HomeActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ProductAuthentifier> call, Throwable t) {
                CommonProvider.showAlert("Failed:" + t.getMessage(), HomeActivity.this);
            }
        });
    }

    private String productAuthMessageFormatter(final ProductAuthentifier result, final String title) {
        return title + "\n\n" +
                "Reference    : " + result.getProductReference() + "\n" +
                "Product name : " + result.getProductName() + "\n" +
                "Manufacturer : " + result.getManufacturer() + "\n" +
                "Made In      : " + result.getMadeIn() + "\n" +
                "Released year: " + result.getYearOfRelease() + "\n";
    }

    private void logout() {
        Intent logoutIntent = new Intent(HomeActivity.this, LoginActivity.class);
        logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(logoutIntent);
        finish();
    }

    private void redirectToLogin() {
        Intent loginIntent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.home_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_about:
                        CommonProvider.showAlertDialog(HomeActivity.this, "ABOUT US", getString(R.string.about_us), 2);
                        return true;
                    case R.id.action_help:
                        CommonProvider.showAlertDialog(HomeActivity.this, "HELP", getString(R.string.help), 2);
                        return true;
                    case R.id.action_logout:
                        logout();
                        return true;
                    default:
                        return false;
                }
            }
        });

        popupMenu.show();
    }

}