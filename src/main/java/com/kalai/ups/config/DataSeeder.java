package com.kalai.ups.config;

import com.kalai.ups.entity.BatteryRating;
import com.kalai.ups.entity.UpsConfig;
import com.kalai.ups.repository.BatteryRatingRepository;
import com.kalai.ups.repository.UpsConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Seeds the Exide discharge rating table (from "Exide Wattage New.pdf") and
 * default UPS configs on first startup. Safe to re-run — checks existence first.
 *
 * Exide table columns: 10, 15, 20, 30, 45, 50, 90, 120, 180, 240, 300, 350, 480, 600 minutes
 * Three ECV sections: 10.8V, 10.5V, 10.2V
 * Battery AH sizes: 18, 26, 28, 34, 42, 55, 65, 75, 84, 100, 120, 130, 150, 160, 200
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private final BatteryRatingRepository batteryRatingRepository;
    private final UpsConfigRepository upsConfigRepository;

    private static final int[] MINUTES = {10, 15, 20, 30, 45, 50, 90, 120, 180, 240, 300, 350, 480, 600};

    @Override
    public void run(ApplicationArguments args) {
        seedBatteryRatings();
        seedUpsConfigs();
    }

    private void seedBatteryRatings() {
        if (batteryRatingRepository.existsByEcv(10.8)) return;

        List<BatteryRating> ratings = new ArrayList<>();

        // ── ECV 10.8V ──────────────────────────────────────────────────────────
        // Source: Exide Wattage New.pdf, ECV 10.8 section
        // Columns: 10, 15, 20, 30, 45, 50, 90, 120, 180, 240, 300, 350, 480, 600 min
        double[][] data108 = {
            // AH   10     15     20     30     45     50     90    120    180    240    300    350    480    600
            { 18,  376,   299,   248,   184,   167,   164,    83,    71,    50,    47,    34,    30,    23,    19},
            { 26,  637,   497,   408,   293,   189,   185,   120,   114,    76,    62,    52,    45,    36,    30},
            { 28,  643,   502,   412,   296,   191,   187,   121,   115,    77,    53,    53,    46,    35,    30},
            { 34,  728,   567,   467,   345,   244,   239,   157,   147,   102,    69,    58,    50,    40,    33},
            { 42,  980,   763,   629,   475,   299,   293,   194,   180,   124,   101,    84,    73,    58,    48},
            { 55, 1192,   929,   766,   579,   401,   393,   254,   243,   162,   132,   110,    96,    76,    53},
            { 65, 1590,  1240,  1022,   772,   479,   470,   300,   292,   191,   155,   130,   113,    90,    74},
            { 75, 1788,  1389,  1150,   864,   537,   526,   347,   328,   216,   178,   150,   131,   111,    84},
            { 84, 1862,  1444,  1196,   900,   594,   582,   388,   362,   238,   195,   155,   144,   112,    92},
            {100, 2373,  1810,  1491,  1125,   696,   682,   462,   423,   294,   240,   200,   174,   138,   114},
            {120, 2788,  2172,  1789,  1350,   834,   818,   554,   508,   353,   248,   240,   209,   156,   137},
            {130, 2839,  2212,  1822,  1375,   904,   886,   601,   550,   382,   312,   250,   226,   179,   148},
            {150, 3485,  2715,  2237,  1588,  1043,  1023,   693,   635,   441,   360,   300,   261,   207,   177},
            {160, 3559,  2780,  2291,  1728,  1113,  1091,   739,   677,   470,   384,   320,   274,   221,   182},
            {200, 4646,  3620,  2982,  2250,  1391,  1364,   924,   846,   588,   480,   400,   348,   276,   224},
        };
        addRatings(ratings, 10.8, data108);

        // ── ECV 10.5V ──────────────────────────────────────────────────────────
        double[][] data105 = {
            { 18,  399,   310,   255,   189,   172,   169,    89,    73,    54,    42,    35,    31,    20,     0},
            { 26,  671,   517,   422,   303,   193,   189,   129,   116,    80,    65,    53,    47,    37,    30},
            { 28,  678,   522,   426,   306,   195,   191,   130,   117,    80,    65,    54,    47,    37,    31},
            { 34,  752,   590,   482,   357,   249,   244,   158,   149,    88,    72,    59,    52,    41,    34},
            { 42, 1001,   794,   650,   491,   305,   299,   208,   183,   129,   105,    85,    76,    59,    49},
            { 55, 1218,   967,   792,   598,   417,   403,   272,   247,   168,   137,   113,    99,    78,    64},
            { 65, 1625,  1290,  1056,   798,   493,   483,   322,   296,   199,   162,   133,   117,    92,    76},
            { 75, 1828,  1445,  1185,   893,   551,   541,   377,   333,   227,   185,   154,   135,   105,    87},
            { 84, 1901,  1505,  1233,   930,   611,   599,   416,   368,   243,   199,   155,   144,   113,    93},
            {100, 2370,  1879,  1537,  1163,   717,   703,   495,   437,   305,   249,   205,   180,   147,   117},
            {120, 2844,  2255,  1844,  1395,   860,   844,   594,   517,   357,   299,   246,   216,   169,   140},
            {130, 2896,  2296,  1878,  1427,   932,   914,   644,   550,   398,   324,   267,   234,   183,   152},
            {150, 3555,  2819,  2306,  1744,  1016,  1055,   743,   646,   459,   374,   308,   270,   212,   176},
            {160, 3640,  2885,  2367,  1786,  1147,  1125,   792,   689,   490,   398,   328,   288,   225,   187},
            {200, 4739,  3758,  3074,  2325,  1434,  1406,   990,   861,   612,   498,   410,   360,   282,   234},
        };
        addRatings(ratings, 10.5, data105);

        // ── ECV 10.2V ──────────────────────────────────────────────────────────
        double[][] data102 = {
            { 18,  421,   377,   262,   194,   174,   171,    95,    73,    54,    44,    36,    32,    25,    20},
            { 26,  705,   535,   436,   313,   197,   193,   137,   121,    83,    67,    55,    48,    37,    31},
            { 28,  712,   541,   440,   315,   199,   195,   139,   118,    84,    68,    55,    49,    38,    32},
            { 34,  777,   612,   498,   369,   254,   249,   180,   151,    89,    72,    58,    52,    40,    33},
            { 42, 1022,   825,   677,   507,   311,   305,   222,   185,   134,   108,    88,    78,    61,    50},
            { 55, 1244,  1005,   817,   617,   427,   413,   290,   250,   175,   142,   116,   102,    79,    66},
            { 65, 1660,  1340,  1090,   823,   506,   495,   343,   300,   207,   168,   137,   127,    94,    78},
            { 75, 1867,  1500,  1220,   921,   566,   555,   395,   337,   239,   194,   158,   140,   108,    90},
            { 84, 1941,  1561,  1270,   960,   628,   616,   444,   373,   253,   205,   167,   148,   114,    95},
            {100, 2416,  1948,  1583,  1200,   738,   724,   528,   438,   318,   258,   210,   186,   144,   120},
            {120, 2899,  2338,  1899,  1440,   885,   869,   634,   526,   382,   310,   252,   223,   173,   144},
            {130, 2952,  2381,  1934,  1466,   960,   947,   685,   570,   413,   335,   273,   242,   187,   155},
            {150, 3624,  2922,  2375,  1800,  1108,  1085,   792,   657,   477,   387,   315,   279,   216,   180},
            {160, 3717,  2992,  2432,  1843,  1182,  1158,   845,   701,   509,   413,   335,   298,   230,   192},
            {200, 4832,  3896,  3166,  2400,  1417,  1448,  1056,   876,   635,   516,   420,   372,   288,   240},
        };
        addRatings(ratings, 10.2, data102);

        batteryRatingRepository.saveAll(ratings);
    }

    private void addRatings(List<BatteryRating> list, double ecv, double[][] data) {
        for (double[] row : data) {
            int ah = (int) row[0];
            for (int i = 0; i < MINUTES.length; i++) {
                if (row[i + 1] <= 0) continue;
                BatteryRating r = new BatteryRating();
                r.setEcv(ecv);
                r.setBatteryAh(ah);
                r.setDischargeMinutes(MINUTES[i]);
                r.setWattsPerBattery(row[i + 1]);
                list.add(r);
            }
        }
    }

    private void seedUpsConfigs() {
        // Seed default 10 kVA and 20 kVA configs if none exist
        // Parameters derived from Excel sheet methodology:
        //   - Load factor 0.8, power factor 0.8 (standard)
        //   - Inverter efficiency 0.96
        //   - 32 batteries per string for 10kVA (384V system), 38 for 20kVA
        if (upsConfigRepository.findByKva(10).isEmpty()) {
            UpsConfig c = new UpsConfig();
            c.setKva(10);
            c.setUpsPrice(100000.0);
            c.setBatteryPrice(2500.0);
            c.setLoadFactor(0.8);
            c.setInverterEfficiency(0.96);
            c.setBatteriesPerString(32);
            c.setNumStrings(1);
            c.setEcv(10.8);
            c.setActive(true);
            upsConfigRepository.save(c);
        }
        if (upsConfigRepository.findByKva(20).isEmpty()) {
            UpsConfig c = new UpsConfig();
            c.setKva(20);
            c.setUpsPrice(200000.0);
            c.setBatteryPrice(2500.0);
            c.setLoadFactor(0.8);
            c.setInverterEfficiency(0.96);
            c.setBatteriesPerString(38);
            c.setNumStrings(1);
            c.setEcv(10.8);
            c.setActive(true);
            upsConfigRepository.save(c);
        }
    }
}
