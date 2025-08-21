package org.correos.app.addressvalidation.domain.service;

import org.correos.app.addressvalidation.application.model.RawAddressToValidate;
import org.correos.app.addressvalidation.domain.model.NormalizedAddress;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class AddressNormalizerServiceImpl implements AddressNormalizerService {

    enum LocaleISO { ES, PT, AD }

    private record Lexicon(Map<String,String> abbr, Set<String> tipos, Pattern cpPattern) {}

    private static Lexicon lexES() {
        return new Lexicon(
                Map.ofEntries(
                        e("C/", "CALLE"), e("CL", "CALLE"), e("C", "CALLE"),
                        e("AVDA", "AVENIDA"), e("AV", "AVENIDA"), e("AV.", "AVENIDA"),
                        e("PZA", "PLAZA"), e("PLZ", "PLAZA"), e("PL", "PLAZA"),e("P.", "PISO"),
                        e("CR", "CARRETERA"), e("CRA", "CARRETERA"), e("CTRA", "CARRETERA"),
                        e("PSO", "PASEO"), e("PG", "PASEO"), e("TRV", "TRAVESIA"), e("TRVA", "TRAVESIA")
                ),
                Set.of("CALLE","AVENIDA","PLAZA","CARRETERA","PASEO","TRAVESIA","CAMINO","RONDA","GLORIETA","BULEVAR"),
                Pattern.compile("\\b\\d{5}\\b")
        );
    }

    private static Lexicon lexPT() {
        return new Lexicon(
                Map.ofEntries(
                        e("R", "RUA"), e("R.", "RUA"),
                        e("AV", "AVENIDA"), e("AV.", "AVENIDA"),
                        e("PC", "PRAÇA"), e("PC.", "PRAÇA"),
                        e("LG", "LARGO"), e("LG.", "LARGO"),
                        e("TV", "TRAVESSA"), e("TV.", "TRAVESSA")
                ),
                Set.of("RUA","AVENIDA","PRAÇA","LARGO","TRAVESSA"),
                Pattern.compile("\\b\\d{4}-\\d{3}\\b")
        );
    }

    private static Lexicon lexAD() {
        return lexES();
    }

    // Para abreviaturas
    private static Map.Entry<String, String> e(String k, String v) {return Map.entry(k, v);}

    // Para números escritos
    private static Map.Entry<String, Integer> e(String k, Integer v) {return Map.entry(k, v);
    }

    private static final Map<String,Integer> ES_NUM = Map.ofEntries(
            e("CERO",0), e("UNO",1), e("UNA",1), e("DOS",2), e("TRES",3), e("CUATRO",4),
            e("CINCO",5), e("SEIS",6), e("SIETE",7), e("OCHO",8), e("NUEVE",9), e("DIEZ",10),
            e("ONCE",11), e("DOCE",12), e("TRECE",13), e("CATORCE",14), e("QUINCE",15),
            e("VEINTE",20), e("TREINTA",30), e("CUARENTA",40), e("CINCUENTA",50),
            e("SESENTA",60), e("SETENTA",70), e("OCHENTA",80), e("NOVENTA",90)
    );

    private static final Map<String,Integer> PT_NUM = Map.ofEntries(
            e("ZERO",0), e("UM",1), e("UMA",1), e("DOIS",2), e("DUAS",2), e("TRES",3),
            e("TRÊS",3), e("QUATRO",4), e("CINCO",5), e("SEIS",6), e("SETE",7), e("OITO",8),
            e("NOVE",9), e("DEZ",10), e("ONZE",11), e("DOZE",12), e("TREZE",13),
            e("CATORZE",14), e("QUINZE",15), e("VINTE",20), e("TRINTA",30),
            e("QUARENTA",40), e("CINQUENTA",50), e("SESSENTA",60), e("SETENTA",70),
            e("OITENTA",80), e("NOVENTA",90)
    );

    @Override
    public NormalizedAddress normalize(RawAddressToValidate input) {
        String raw = Optional.ofNullable(input.rawText()).orElse("").trim();
        if (raw.isEmpty()) return empty("ES");

        String up = preclean(raw);
        LocaleISO locale = detectLocale(up, input.localeHint());
        Lexicon lex = switch (locale) { case PT -> lexPT(); case AD -> lexAD(); default -> lexES(); };

        String expanded = expandAbbr(up, lex);
        String cp = matchFirst(lex.cpPattern(), expanded);
        String pais = detectCountry(expanded, locale);

        Complements comps = extractComplements(expanded, locale);
        StreetParts st = extractStreet(expanded, lex);

        String numeroVia = normalizeNumToken(st.numero, locale);
        String planta = normalizeFloor(comps.planta, locale);
        String puerta = normalizeDoor(comps.puerta);
        CityParts city = extractCity(expanded, cp, pais);
        double confidence = score(st, cp, city, pais, comps);

        return new NormalizedAddress(
                st.tipo,
                st.nombre,
                numeroVia,
                planta,
                puerta,
                cp,
                city.localidad,
                city.provincia,
                pais,
                comps.observaciones,
                comps.extras,
                confidence, locale.name()
        );
    }

    // ------------------ Helpers -------------------

    private String preclean(String s) {
        String t = s.replaceAll("[\\t\\n]+"," ").replaceAll("\\s{2,}"," ").trim();
        return t.replace("º","").replace("°","").replaceAll("\\s*,\\s*", ", ");
    }

    private LocaleISO detectLocale(String s, String hint) {
        if (hint != null) {
            try { return LocaleISO.valueOf(hint.toUpperCase()); } catch (Exception ignored) {}
        }
        String u = s.toUpperCase();
        if (u.matches(".*\\b\\d{4}-\\d{3}\\b.*") || u.contains("PORTUGAL")) return LocaleISO.PT;
        if (u.contains("ANDORRA") || u.matches(".*\\bAD\\d{3}\\b.*")) return LocaleISO.AD;
        return LocaleISO.ES;
    }

    private String expandAbbr(String s, Lexicon lex) {
        String u = s.toUpperCase()
                .replace(".", "")
                .replace("/", " ")
                .replace("º", "")
                .replace("°", "")
                .replaceAll("\\s{2,}", " ")
                .replace("Á", "A").replace("É", "E").replace("Í", "I")
                .replace("Ó", "O").replace("Ú", "U").replace("Ç", "C")
                .replaceAll("\\bSTA\\b", "SANTA")
                .replaceAll("\\bST\\b", "SAN")
                .replaceAll("S/N", " SN ");

        for (var e : lex.abbr().entrySet()) {
            u = u.replaceAll("\\b" + Pattern.quote(e.getKey()) + "\\b", e.getValue());
        }

        return u.replaceAll("\\s{2,}", " ").trim();
    }

    private String matchFirst(Pattern p, String s) {
        Matcher m = p.matcher(s); return m.find()? m.group(): null;
    }

    private String detectCountry(String s, LocaleISO guess) {
        String u = s.toUpperCase();
        if (u.contains("PORTUGAL")) return "PORTUGAL";
        if (u.contains("ANDORRA")) return "ANDORRA";
        if (u.contains("ESPAÑA") || u.contains("SPAIN") || guess==LocaleISO.ES) return "ESPAÑA";
        return null;
    }

    private record Complements(String planta, String puerta, Map<String,String> extras, String observaciones) {}

    private Complements extractComplements(String s, LocaleISO locale) {
        Map<String,String> extras = new HashMap<>();

        String planta = firstGroup(s, "\\b(PLANTA|PISO|ANDAR)\\s*([0-9A-Z]+)\\b", 2);
        String puerta = firstGroup(s, "\\b(PUERTA|PORTA|DTO|DTO\\.?|DCHA|DCH|DER|IZQ|ESQ|ESQ\\.?|FRAC(C|Ç)AO)\\s*([0-9A-Z]+)\\b", 3);
        extras.put("portal", firstGroup(s, "\\b(PORTAL|BLOQUE|LOTE)\\s*([0-9A-Z]+)\\b", 2));
        extras.put("escalera", firstGroup(s, "\\b(ESCALERA|ESC)\\s*([0-9A-Z]+)\\b", 2));
        extras.put("km", firstGroup(s, "\\bKM\\s*([0-9]+([.,][0-9]+)?)\\b", 1));
        extras.put("sn", s.contains(" SN ") ? "true" : "false");

        String obs = null;
        Matcher m = Pattern.compile("(FR(ENTE)?\\s+A\\s+|ENFRENTE\\s+DE\\s+|JUNTO\\s+A\\s+|CERCA\\s+DE\\s+)(.+)$").matcher(s);
        if (m.find()) obs = m.group().trim();
        return new Complements(planta, puerta, extras, obs);
    }

    private String firstGroup(String s, String regex, int idx) {
        Matcher m = Pattern.compile(regex).matcher(s);
        return m.find()? m.group(idx).toUpperCase() : null;
    }

    private record StreetParts(String tipo, String nombre, String numero) {}

    private StreetParts extractStreet(String s, Lexicon lex) {
        String tipos = String.join("|", lex.tipos());
        Pattern p1 = Pattern.compile("\\b(" + tipos + ")\\b\\s+((?:[A-ZÀ-ÿ\\s]+?))\\s+(\\d+[A-Z]?)\\b");
        Matcher m1 = p1.matcher(s);
        if (m1.find()) return new StreetParts(m1.group(1), m1.group(2).trim(), m1.group(3));

        Pattern p2 = Pattern.compile("([^,]+?)\\s*,\\s*(\\d+[A-Z]?)\\b");
        Matcher m2 = p2.matcher(s);
        if (m2.find()) return new StreetParts(null, m2.group(1).trim(), m2.group(2));

        return new StreetParts(null, null, null);
    }


//    private StreetParts extractStreet(String s, Lexicon lex) {
//        String tipos = String.join("|", lex.tipos());
//
//        Pattern p1 = Pattern.compile("\\b(" + tipos + ")\\b\\s+([^,]+?)\\s*(?:,\\s*|\\s+)(\\d+[A-Z]?)?\\b");
//        Matcher m1 = p1.matcher(s);
//        if (m1.find()) return new StreetParts(m1.group(1), m1.group(2).trim(), m1.group(3));
//
//        Pattern p2 = Pattern.compile("([^,]+?)\\s*,\\s*(\\d+[A-Z]?)\\b");
//        Matcher m2 = p2.matcher(s);
//        if (m2.find()) return new StreetParts(null, m2.group(1).trim(), m2.group(2));
//
//        return new StreetParts(null, null, null);
//    }

    private String normalizeNumToken(String token, LocaleISO locale) {
        if (token == null) return null;
        return wordsToDigits(token.trim().toUpperCase(), locale);
    }

    private String wordsToDigits(String t, LocaleISO locale) {
        Map<String,Integer> base = (locale==LocaleISO.PT) ? PT_NUM : ES_NUM;
        String u = t.replace("Ê","E").replace("É","E").replace("Ã","A").replace("À","A");
        if (u.contains(" E ")) {
            int sum = 0; boolean ok=true;
            for (String part : u.split("\\s+E\\s+")) {
                Integer v = base.get(part);
                if (v==null) { ok=false; break; }
                sum += v;
            }
            if (ok) return Integer.toString(sum);
        }
        if (u.startsWith("VEINTI")) {
            Integer ones = base.get(u.substring(6));
            if (ones!=null) return Integer.toString(20+ones);
        }
        Integer v = base.get(u);
        return v!=null ? Integer.toString(v) : t;
    }

    private String normalizeFloor(String planta, LocaleISO locale) {
        if (planta == null) return null;
        if (planta.equalsIgnoreCase("BAJO") || planta.equalsIgnoreCase("BJO") ||
                planta.equalsIgnoreCase("R/C") || planta.toUpperCase().contains("RES")) return "0";
        return wordsToDigits(planta, locale);
    }

    private String normalizeDoor(String puerta) {
        if (puerta == null) return null;
        return puerta.toUpperCase().replaceAll("\\s+","");
    }

    private record CityParts(String localidad, String provincia) {}

    private CityParts extractCity(String s, String cp, String pais) {
        if (cp != null) {
            String tail = s.substring(s.indexOf(cp) + cp.length()).replace(","," ").trim();
            String[] parts = tail.split("\\s+");
            String loc = parts.length>0 ? parts[0] : null;
            return new CityParts(loc, null);
        }
        if (pais != null && s.toUpperCase().contains(pais)) {
            String head = s.toUpperCase().substring(0, s.toUpperCase().indexOf(pais)).trim();
            String[] tokens = head.split(",\\s*");
            String loc = tokens.length>0 ? tokens[tokens.length-1].trim() : null;
            return new CityParts(loc, null);
        }
        return new CityParts(null, null);
    }

    private double score(StreetParts st, String cp, CityParts city, String pais, Complements comps) {
        double sc = 0;
        if (st.nombre != null) sc += 0.3;
        if (st.numero != null || "true".equalsIgnoreCase(comps.extras.getOrDefault("sn","false"))) sc += 0.25;
        if (cp != null) sc += 0.2;
        if (city.localidad != null) sc += 0.15;
        if (pais != null) sc += 0.1;
        return Math.min(1.0, sc);
    }

    private NormalizedAddress empty(String locale) {
        return new NormalizedAddress(null,null,null,null,null,null,null,null,null,null, Map.of(),0.0, locale);
    }
}
