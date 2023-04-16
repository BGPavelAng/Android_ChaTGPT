
class MainActivity : AppCompatActivity() {

    private val JSON: MediaType = "application/json; charset=utf-8".toMediaTypeOrNull()!!
    private var nuevoCall = OkHttpClient()

    private fun addResponse(response: String?) {
        Handler(Looper.getMainLooper()).post {
            val wriText = findViewById<TextView>(R.id.editTextTextMultiLine)
            wriText.setKeyListener(null);
            wriText.setMovementMethod(ScrollingMovementMethod())

            wriText.append("\n")
            wriText.append(response)
        }
    }

    private fun comenzarPlatica(pregunta: String?){

        val sendQu = JSONObject()
        val linkToURL = "https://api.openai.com/v1/completions"
        val wriText = findViewById<TextView>(R.id.editTextTextMultiLine)

        wriText.append("\n\n")
        wriText.append(pregunta)


        try {
            sendQu.put("model", "text-davinci-003")
            sendQu.put("prompt", pregunta)
            sendQu.put("max_tokens", 2048)
        } catch (e: JSONException) {
            throw RuntimeException(e)
        }

        val requestBd: RequestBody = sendQu.toString().toRequestBody(JSON)
        val request: Request = Request.Builder()

            .url(linkToURL)
            .header("Authorization", "Bearer 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")
            .post(requestBd)
            .build()
        nuevoCall.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    var jsObct: JSONObject? = null
                    try {
                        jsObct = JSONObject(response.body!!.string())
                        val jsonArray = jsObct.getJSONArray("choices")
                        val result = jsonArray.getJSONObject(0).getString("text")
                        addResponse(result.toString())
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                } else {
                    addResponse(response.body.toString())
                }
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val btnEnviar = findViewById<Button>(R.id.button)
        val txtContexto = findViewById<EditText>(R.id.editTextTextPersonName)

        txtContexto.setOnClickListener{
            txtContexto.text = null
        }

        btnEnviar.setOnClickListener{

            Toast.makeText(applicationContext, "Enviando pregunta.", Toast.LENGTH_SHORT).show()

            comenzarPlatica(txtContexto.text.toString().trim())


        }

    }

}

