package com.gojavas.taskforce.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gojavas.taskforce.R;

public class PrivacyPolicyActivity extends Activity {
    String PNP_ACCEPTED = "PNP_ACCEPTED";
    String CALLED_FROM;
    protected Context appContext= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TextView tncDetails;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        Intent iin= getIntent();
        appContext = getApplicationContext();
        Bundle b = iin.getExtras();

        if(b!=null)
        {
            CALLED_FROM =(String) b.get("From");

        }

        if(!CALLED_FROM.equalsIgnoreCase("Login")){
            ((Button)findViewById(R.id.iAgreeButton)).setVisibility(View.INVISIBLE);
        }
        String text = "<div class=\"custom-container\">\n" +
                "            <div id=\"myTabContent\" class=\"tab-content\">\n" +
                "                <div class=\"container-fluid\">\n" +
                "                    <div class=\"inner-content\">\n" +
                "                        <div class=\"inner-heading\">\n" +
                "                            <h2 class=\"text-left\"><span style=\"color:#33ccff\">Privacy Policy</span><strong>, E-Commerce Delivery, Courier, Freight, Truck/Lorry</strong></h2>\n" +
                "                            <h6>Quickdel Logistics Pvt Ltd</h6>\n" +
                "                        </div>\n" +
                "                        <div class=\"inner-content\" style=\"color:#616b81;\">\n" +
                "                            <p>\n" +
                "                                We value the trust you place on us. That's why we insist upon the highest standards for secure transactions and\n" +
                "                                customer information privacy. Please read the following statement to learn about our information gathering and\n" +
                "                                dissemination practices.\n" +
                "                            </p>\n" +
                "                            <p>\n" +
                "                                Company has created this privacy policy in order to demonstrate its firm commitment to privacy of its\n" +
                "                                customers. Customer privacy is very important to us. Accordingly, we have developed this Policy in order for\n" +
                "                                you to understand how we collect, use, communicate and disclose and make use of such personal information.\n" +
                "                            </p>\n" +
                "                            <p>\n" +
                "                                <strong>Note:</strong> Our privacy policy is subject to change at any time without notice. To make sure\n" +
                "                                you are aware of any changes, please review this policy periodically. By visiting this Website you agree to be bound by the terms and\n" +
                "                                conditions of this Privacy Policy. If you do not agree please do not use or access our Website. By mere use of the Website,\n" +
                "                                you expressly consent to our use and disclosure of your personal information in accordance with this Privacy Policy. This Privacy Policy is\n" +
                "                                incorporated into and subject to the Terms of Use. The following outlines our privacy policy.\n" +
                "                                </p><ul class=\"text-left\">\n" +
                "                                    <li>\n" +
                "                                        Before or at the time of collecting personal information, we will identify the purposes for which information is being collected\n" +
                "                                    </li>\n" +
                "                                    <li>\n" +
                "                                        We will collect and use personal information solely with the objective of fulfilling those purposes specified by us and for other compatible purposes, unless we obtain the consent of the individual concerned or as required by law\n" +
                "                                    </li>\n" +
                "                                    <li>\n" +
                "                                        We will only retain personal information as long as necessary for the fulfillment of those purposes\n" +
                "                                    </li>\n" +
                "                                    <li>\n" +
                "                                        We will collect personal information by lawful and fair means, where appropriate, with the knowledge or consent of the individual concerned\n" +
                "                                    </li>\n" +
                "                                    <li>\n" +
                "                                        Personal data should be relevant to the purposes for which it is disclosed to us and to be used; to the extent necessary for those purposes, should be accurate, complete, and up-to-date at the time of such disclosure\n" +
                "                                    </li>\n" +
                "                                    <li>\n" +
                "                                        We will protect personal information by reasonable security safeguards against loss or theft, as well as unauthorized access, disclosure, copy, use or modification as the case may be within the circumstances which are under our control\n" +
                "                                    </li>\n" +
                "                                    <li>\n" +
                "                                        We will make readily available to customers, information about our policies and practices relating to the management of personal information\n" +
                "                                    </li>\n" +
                "                                    <li>\n" +
                "                                        If at any point of time you would not like us to retain your personal information, kindly send us a written intimation for the same and we will remove the same from our database. In such a case of removal, if you wish to avail our services in future, you will be required to provide your information a fresh in order to provide you our services.\n" +
                "                                    </li>\n" +
                "                                    <li>\n" +
                "                                        We are committed to conduct our business in accordance with sound principles in order to ensure that the confidentiality of personal information is protected and maintained. No one except the Company and its authorized representatives/agents will have access to the personal information gathered through Company’s website and offices. Company is the possessor of the information gathered and does not trade, share, or rent any information obtained by the Company to any third party except for offers of the Company which are published from time to time.\n" +
                "                                    </li>\n" +
                "                                </ul>\n" +
                "                            <p></p>\n" +
                "                            <h2 class=\"text-left\"><strong>Use of Cookies</strong></h2>\n" +
                "                            <p>\n" +
                "                                A cookie is a piece of data stored on the user's hard drive containing information about the user. Company uses cookies for maintaining privacy policies, security, session continuity, and customization purposes. If a user rejects a cookie, he/she may still use some information put on the website of the Company, but may not be able to gain access to some of the Services or use some features of the website.\n" +
                "                                Company uses client IP addresses to analyze the usage of site, administer the site, track users' movements within the website of the Company, and gather broad demographic information for aggregate use. Links to other sites This site contains links to other sites. Company is not responsible for the privacy practices or the content of such web sites. This links are provided only for convenience and Company does not have any relationship with such websites. If you have any queries about privacy policy of the Company,\n" +
                "                                its practices on this site, or your dealings with this web site, please contact or send an email to the following ID:\n" +
                "                                </p><ul class=\"text-left\">\n" +
                "                                    <li>\n" +
                "                                        <h5><strong>Sharing of Personal Information</strong></h5>\n" +
                "                                    </li>\n" +
                "                                    <ul>\n" +
                "                                        <li>\n" +
                "                                            We may share personal information with our other corporate entities and affiliates to help detect and prevent identity theft, fraud and other potentially illegal acts, correlate related or multiple accounts to prevent abuse of our services, and to facilitate joint or co-branded services that you request where such services are provided by more than one corporate entity. Those entities and affiliates may not market to you as a result of such sharing unless you explicitly opt-in.\n" +
                "                                        </li>\n" +
                "                                        <li>\n" +
                "                                            We may disclose personal information if required to do so by law or in the good faith belief that such disclosure is reasonably necessary to respond to subpoenas, court\n" +
                "                                        </li>\n" +
                "                                        <li>\n" +
                "                                            Orders or other legal process. We may disclose personal information to law enforcement offices, third party rights owners, or others in the good faith belief that such disclosure is reasonably necessary to: enforce our Terms or Privacy Policy, respond to claims that an advertisement, posting or other content violates the rights of a third party, or protect the rights, property or personal safety of our users or the general public.\n" +
                "                                        </li>\n" +
                "                                        <li>\n" +
                "                                            We and our affiliates will share / sell some or all of your personal information with another business entity we (or our assets) plan to merge with, or be acquired by that business entity, or re-organization, amalgamation, restructuring of business. Such a transaction occurs that other business entity (or the new combined entity) will be required to follow this privacy policy with respect to your personal information.\n" +
                "                                        </li>\n" +
                "                                    </ul>\n" +
                "                                    <li>\n" +
                "                                        <h5><strong>Security</strong></h5>\n" +
                "                                    </li>\n" +
                "                                    <ul>\n" +
                "                                        <li>\n" +
                "                                            Our Website has stringent security measures in place to protect the loss, misuse, and alteration of the information under our control. Whenever you change or access your account information, we offer the use of a secure server. Once your information is in our possession we adhere to strict security guidelines, protecting it against unauthorized access.\n" +
                "                                        </li>\n" +
                "                                    </ul>\n" +
                "                                    <li>\n" +
                "                                        <h5><strong>Links to other websites</strong></h5>\n" +
                "                                    </li>\n" +
                "                                    <ul>\n" +
                "                                        <li>\n" +
                "                                            Our website may contain links to other websites of interest. However, once you have used these links to leave our site, you should note that we do not have any control over that other website. Therefore, we cannot be responsible for the protection and privacy of any information which you provide whilst visiting such sites and such sites are not governed by this privacy statement. You should exercise caution and look at the privacy statement applicable to the website in question.\n" +
                "                                        </li>\n" +
                "                                    </ul>\n" +
                "                                    <li>\n" +
                "                                        <h5><strong>Your Consent</strong></h5>\n" +
                "                                    </li>\n" +
                "                                    <ul>\n" +
                "                                        <li>\n" +
                "                                            By using the Website and/ or by providing your information, you consent to the collection and use of the information you disclose on the Website in accordance with this Privacy Policy, including but not limited to your consent for sharing your information as per this privacy policy.\n" +
                "                                        </li>\n" +
                "                                        <li>\n" +
                "                                            If we decide to change our privacy policy, we will post those changes on this page so that you are always aware of what information we collect, how we use it, and under what circumstances we disclose it.\n" +
                "                                        </li>\n" +
                "                                    </ul>\n" +
                "                                </ul>\n" +
                "                            <p></p>\n" +
                "                            <h2 class=\"text-left\"><strong>Grievance Officer</strong></h2>\n" +
                "                            <p>\n" +
                "                                If there are any questions regarding this privacy policy you may <a href=\"contact.html\" target=\"_self\">contact us</a> using the information below or drop us a mail with subject line-PRIVACY POLICY:\n" +
                "\n" +
                "                            </p>\n" +
                "                            <div class=\"text-left\">\n" +
                "                                <strong>Quickdel Logistics Pvt Ltd</strong>\n" +
                "                                <div>\n" +
                "                                    <strong>Address:</strong>\n" +
                "                                    <div>\n" +
                "                                        <div>\n" +
                "                                            1101, Spaze I Tech Park, Tower - A, Sector - 49,\n" +
                "                                        </div>\n" +
                "                                        <div>\n" +
                "                                            Sohna Road, Gurgaon,\n" +
                "                                        </div>\n" +
                "                                        <div>Haryana - 122018.</div>\n" +
                "                                    </div>\n" +
                "                                </div>\n" +
                "                                <div>\n" +
                "                                    <strong>\n" +
                "                                        Phone:\n" +
                "                                    </strong>\n" +
                "                                    011-40113440\n" +
                "                                </div>\n" +
                "                                <div>\n" +
                "                                    <strong>\n" +
                "                                        Web:\n" +
                "                                    </strong>\n" +
                "                                    www.Gojavas.com\n" +
                "                                </div>\n" +
                "                                <div>\n" +
                "                                    <strong>Email:</strong>it@gojavas.com\n" +
                "                                </div>\n" +
                "                            </div>\n" +
                "                        </div>\n" +
                "                    </div>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "        </div>";
        tncDetails = (TextView) findViewById(R.id.tncText);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            tncDetails.setText(Html.fromHtml(text,Html.FROM_HTML_MODE_LEGACY));
        } else {
            tncDetails.setText(Html.fromHtml(text));
        }


        ((Button)findViewById(R.id.iAgreeButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences shared = appContext.getSharedPreferences("PrivacyPolicy",MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                editor.putString("IsSavedPP","OK");
                editor.commit();
                finish();
            }});

    }


    @Override
    public void onBackPressed() {
        if(CALLED_FROM.equalsIgnoreCase("Login")){

        }else{
            super.onBackPressed();
        }
    }
}
