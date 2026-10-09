package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [LetterEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun letterDao(): LetterDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ai_application_letter.db"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            INSTANCE?.letterDao()?.insertAll(getInitialLetters())
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }

        private fun getInitialLetters(): List<LetterEntity> {
            val now = System.currentTimeMillis()
            return listOf(
                LetterEntity(
                    title = "बीमारी के कारण अवकाश",
                    category = "SICK_LEAVE",
                    recipient = "श्रीमान प्रधानाचार्य महोदय",
                    institution = "राजकीय उच्चतर माध्यमिक विद्यालय, नई दिल्ली",
                    applicantName = "राहुल शर्मा (कक्षा 10वीं - 'अ')",
                    subject = "बीमारी के कारण अवकाश हेतु प्रार्थना पत्र",
                    content = """सेवा में,
श्रीमान प्रधानाचार्य महोदय,
राजकीय उच्चतर माध्यमिक विद्यालय, नई दिल्ली

विषय: बीमारी के कारण अवकाश हेतु आवेदन।

महोदय,
सविनय निवेदन है कि मैं राहुल शर्मा आपकी कक्षा 10वीं 'अ' का छात्र हूँ। मुझे कल रात से अचानक तीव्र ज्वर (बुखार) और सिरदर्द हो गया है। चिकित्सक ने मुझे पूर्ण विश्राम एवं स्वास्थ्य लाभ लेने का परामर्श दिया है।

अतः आपसे करबद्ध प्रार्थना है कि मुझे दिनांक [12 Oct] से [15 Oct] (कुल 3 दिन) का अवकाश प्रदान करने की कृपा करें। इसके लिए मैं सदैव आपका आभारी रहूँगा।

धन्यवाद।

भवदीय,
राहुल शर्मा
कक्षा: 10वीं 'अ', अनुक्रमांक: 24
दिनांक: 12/10/2026""",
                    language = "hi",
                    dateString = "12 Oct 2026",
                    timestamp = now - (1000 * 60 * 60 * 2), // 2 hours ago
                    status = "स्वीकृत",
                    isFavorite = true,
                    includeMedicalCert = true,
                    leaveDays = 3
                ),
                LetterEntity(
                    title = "विवाह समारोह अवकाश",
                    category = "SCHOOL_LEAVE",
                    recipient = "श्रीमान प्रधानाचार्य महोदय",
                    institution = "आदर्श विद्या मंदिर, जयपुर",
                    applicantName = "राहुल शर्मा (कक्षा 10वीं - 'अ')",
                    subject = "बड़ी बहन के विवाह समारोह में सम्मिलित होने हेतु अवकाश",
                    content = """सेवा में,
श्रीमान प्रधानाचार्य महोदय,
आदर्श विद्या मंदिर, जयपुर

विषय: आवश्यक पारिवारिक विवाह समारोह हेतु अवकाश प्रार्थना।

महोदय,
सादर निवेदन है कि मेरी बड़ी बहन का शुभ विवाह आगामी दिनांक 14 अक्टूबर 2026 को निश्चित हुआ है। परिवार का ज्येष्ठ पुत्र होने के नाते विवाह के समस्त आवश्यक प्रबंधों एवं रस्मों में मेरा उपस्थित रहना अनिवार्य है।

अतः प्रार्थना है कि मुझे दिनांक [14 Oct 2026] से [18 Oct 2026] तक (कुल 5 दिन) का आकस्मिक अवकाश स्वीकृत करने का अनुग्रह करें।

सधन्यवाद।

आपका आज्ञाकारी शिष्य,
राहुल शर्मा
कक्षा: 10वीं 'अ'
दिनांक: 10/10/2026""",
                    language = "hi",
                    dateString = "10 Oct 2026",
                    timestamp = now - (1000 * 60 * 60 * 24 * 2), // 2 days ago
                    status = "स्वीकृत",
                    isFavorite = false,
                    includeMedicalCert = false,
                    leaveDays = 5
                ),
                LetterEntity(
                    title = "शुल्क माफी हेतु प्रार्थना",
                    category = "FEE_CONCESSION",
                    recipient = "श्रीमान प्रधानाचार्य महोदय",
                    institution = "केंद्रीय विद्यालय, भोपाल",
                    applicantName = "अनन्या कुमारी (कक्षा 9वीं)",
                    subject = "वार्षिक शिक्षण शुल्क में रियायत / पूर्ण मुक्ति हेतु",
                    content = """सेवा में,
श्रीमान प्रधानाचार्य महोदय,
केंद्रीय विद्यालय क्रमांक-1, भोपाल

विषय: शिक्षण शुल्क मुक्ति / रियायत हेतु आवेदन।

महोदय,
सविनय निवेदन है कि मैं आपके विद्यालय की कक्षा 9वीं 'ब' की छात्रा हूँ। मैं विगत कक्षाओं में सदैव प्रथम श्रेणी से उत्तीर्ण होती रही हूँ। मेरे पिताजी एक साधारण दैनिक वेतनभोगी श्रमिक हैं और उनकी अल्प आय से 5 सदस्यों के परिवार का निर्वहन अत्यंत कठिनता से हो पाता है।

अतः आपसे करबद्ध निवेदन है कि मेरी आर्थिक विषमता तथा अध्ययन में निरंतर रुचि को दृष्टिगत रखते हुए मेरा इस सत्र का शिक्षण शुल्क माफ करने की कृपा करें, ताकि मैं अपनी शिक्षा अनवरत जारी रख सकूँ।

धन्यवाद सहित।

आपकी आज्ञाकारिणी शिष्या,
अनन्या कुमारी
कक्षा: 9वीं 'ब'
दिनांक: 28/09/2026""",
                    language = "hi",
                    dateString = "28 Sep 2026",
                    timestamp = now - (1000 * 60 * 60 * 24 * 5),
                    status = "सहेजा गया",
                    isFavorite = true,
                    includeMedicalCert = false,
                    leaveDays = 0
                ),
                LetterEntity(
                    title = "अनुभव प्रमाण पत्र हेतु आवेदन",
                    category = "JOB_APPLICATION",
                    recipient = "श्रीमान मानव संसाधन प्रबंधक (HR)",
                    institution = "ग्लोबल टेक सॉल्यूशंस, पुणे",
                    applicantName = "अमित कुमार (सहायक लेखाकार)",
                    subject = "कार्य अनुभव प्रमाण पत्र (Work Experience Certificate) जारी करने हेतु",
                    content = """To,
The Human Resources Manager,
Global Tech Solutions Pvt. Ltd., Pune

Subject: Request for issuance of Work Experience & Relieving Certificate.

Respected Sir/Madam,
I have had the privilege of working with Global Tech Solutions as an Assistant Accountant for the past 2 years (from October 2024 to September 2026). During this tenure, I performed all entrusted accounting duties with complete diligence and integrity.

As I am pursuing advanced career credentials, I kindly request you to issue my official Work Experience Certificate along with the Relieving Letter.

Thanking you for your kind support and mentorship during my tenure.

Yours sincerely,
Amit Kumar
Employee ID: GTS-4092
Contact: +91 98765 43210
Date: 05/10/2026""",
                    language = "en",
                    dateString = "05 Oct 2026",
                    timestamp = now - (1000 * 60 * 60 * 24 * 7),
                    status = "स्वीकृत",
                    isFavorite = false,
                    includeMedicalCert = false,
                    leaveDays = 0
                )
            )
        }
    }
}
