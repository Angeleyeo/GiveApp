package com.example.giveapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.craftman.cardform.Card;
import com.craftman.cardform.CardForm;
import com.craftman.cardform.OnPayBtnClickListner;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

public class CreditCard extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth fAuth;
    private FirebaseUser user;
    ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_card);

        CardForm cardForm = findViewById(R.id.card_form);
        TextView tenCents = findViewById(R.id.payment_amount);
        Button btnSave = findViewById(R.id.btn_pay);

        tenCents.setText("$0.10");
        btnSave.setText("SAVE CREDIT CARD DETAILS");

        db = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        user = fAuth.getCurrentUser();

        backBtn = findViewById(R.id.backBtn);

        cardForm.setPayBtnClickListner(new OnPayBtnClickListner() {
            @Override
            public void onClick(Card card) {

                String cardName = card.getName();
                String cardNum = card.getNumber();
                String last4 = card.getLast4();
                int expMth = card.getExpMonth();
                int expYr = card.getExpYear();
                String CCV = card.getCVC();
                String logoUrl = "";

                if (Integer.parseInt(last4) / 1000 == 3) {
                    logoUrl = "https://www.google.com.sg/imgres?imgurl=https%3A%2F%2Ficon2.cleanpng.com%2F20180627%2Ffrj%2Fkisspng-centurion-card-american-express-credit-card-discov-amex-5b34540ae87df3.3937418015301560429523.jpg&imgrefurl=https%3A%2F%2Fwww.cleanpng.com%2Ffree%2Famerican-express.html&tbnid=qjcbSeVsnoTLlM&vet=12ahUKEwj66eCR_MnqAhXPM7cAHZqSBUMQMygDegUIARDCAQ..i&docid=7Yy5BzanRfQm2M&w=260&h=200&q=amex%20logo%20png&hl=en&authuser=0&ved=2ahUKEwj66eCR_MnqAhXPM7cAHZqSBUMQMygDegUIARDCAQ";
                } else if (Integer.parseInt(last4) / 1000 == 4) {
                    logoUrl = "https://www.google.com.sg/imgres?imgurl=https%3A%2F%2Fbanner2.cleanpng.com%2F20180810%2Fuqi%2Fkisspng-credit-card-visa-logo-mastercard-visa-logo-svg-vector-amp-png-transparent-vecto-5b6e4791bac489.845100331533953937765.jpg&imgrefurl=https%3A%2F%2Fwww.cleanpng.com%2Fpng-credit-card-visa-logo-mastercard-visa-logo-svg-vec-6259577%2F&tbnid=nGMhCqx4zZjdiM&vet=12ahUKEwiE7O3--snqAhUwkUsFHWWFBlkQMygBegUIARDRAQ..i&docid=X6GZrLJgiGP9DM&w=900&h=900&q=visa%20png&hl=en&authuser=0&ved=2ahUKEwiE7O3--snqAhUwkUsFHWWFBlkQMygBegUIARDRAQ";
                } else if (Integer.parseInt(last4) / 1000 == 5) {
                    logoUrl = "https://www.google.com.sg/imgres?imgurl=https%3A%2F%2Fbanner2.cleanpng.com%2F20180403%2Fqte%2Fkisspng-mastercard-credit-card-visa-payment-service-mastercard-5ac3fae6d9ece0.7626666215227931908926.jpg&imgrefurl=https%3A%2F%2Fwww.cleanpng.com%2Fpng-mastercard-credit-card-visa-payment-service-master-873436%2F&tbnid=1JTg0q7PydxSVM&vet=12ahUKEwjc0arp-8nqAhU1NrcAHQ0IBw4QMygEegUIARDLAQ..i&docid=F6c9tzEDf9ZlxM&w=900&h=580&q=mastercard%20logo%20png&hl=en&authuser=0&ved=2ahUKEwjc0arp-8nqAhU1NrcAHQ0IBw4QMygEegUIARDLAQ";
                }

                CreditCardDetails c = new CreditCardDetails(cardName, cardNum, last4, expMth, expYr, CCV, logoUrl);

                db.collection("users").document(user.getUid()).collection("creditCards").add(c);

                Toast.makeText(CreditCard.this, "Credit Card Details Saved", Toast.LENGTH_LONG).show();
                Intent i = new Intent(CreditCard.this, CreditCardList.class);
                finish();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CreditCard.this, CreditCardList.class);
                startActivity(i);
            }
        });

    }
}