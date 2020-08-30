package com.coronaquiz

import com.coronaquiz.dataClasses.Question

class Mock {
    val questions = listOf(
        Question(
            mapOf(
                Pair("es", "¿En cuál ciudad de China se originó el Coronavirus?")
            ),
            mapOf(
                Pair(
                    "es",
                    listOf(
                        "Wuhan",
                        "Beijing",
                        "Pekin"
                    )
                )
            ),
            0
        ),
        Question(
            mapOf(
                Pair("es", "¿Qué día se declaró el Coronavirus como Pandemia mundial?")
            ),
            mapOf(
                Pair(
                    "es",
                    listOf(
                        "3 de enero de 2020",
                        "27 de diciembre de 2019"
                    )
                )
            ),
            1
        ),
        Question(
            mapOf(
                Pair("es", "¿Qué país ha presentado una mayor cantidad de casos fatales?")
            ),
            mapOf(
                Pair(
                    "es",
                    listOf(
                        "Alemania",
                        "España",
                        "Italia",
                        "China"
                    )
                )
            ),
            2
        )
    )
}