package com.barun.covid;

import com.barun.covid.pojo.Availability;
import com.barun.covid.pojo.Root;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class App {

    public Properties getProperties() throws IOException {
        Properties properties = new Properties();
        ClassLoader cl = this.getClass().getClassLoader();
        String filePath = cl.getResource("application.properties").getFile();

        BufferedInputStream bis = new BufferedInputStream(
                new FileInputStream(filePath));
        properties.load(bis);
        return properties;
    }

    public Set<String> getNextNDates(String inputDate, int N) {
        String[] splits = inputDate.split("-");
        Integer day = Integer.valueOf(splits[0]);
        Integer month = Integer.valueOf(splits[1]);
        Integer year = Integer.valueOf(splits[2]);
        Calendar start = Calendar.getInstance();
        start.set(year, month - 1, day);

        Set<String> dates = new HashSet<>();
        SimpleDateFormat sdf = new SimpleDateFormat("d-MM-yyyy");

        dates.add(sdf.format(start.getTime()));
        for (int i = 1; i <= N; i++) {
            start.add(Calendar.DATE, 1);
            Date dt = start.getTime();
            dates.add(sdf.format(dt));
        }
        return dates;
    }

    public Set<Availability> getVaccinationSlotOnDate(String pinCode,
            String date)
            throws IOException {
        OkHttpClient client = new OkHttpClient();

        String url = String
                .format("https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/calendarByPin?pincode=%s&date=%s",
                        pinCode, date);
        Request request = new Request.Builder()
                .url(url)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        ResponseBody responseBody = client.newCall(request).execute()
                .body();
        Root root = objectMapper
                .readValue(responseBody.string(), Root.class);

        Set<Availability> availability = root.getCenters().stream()
                .flatMap(center -> center.getSessions()
                        .stream()
                        .filter(session ->
                                session.getAvailable_capacity() > 0
                                        &&
                                        session.getMin_age_limit() == 18)
                        .map(session -> new Availability(center.getName(),
                                session.getDate())))
                .collect(Collectors.toSet());
        return availability;
    }

    private void sendMail(String to, Set<Availability> availabilities,
            Properties properties)
            throws MessagingException {
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.user", properties.getProperty("mail.username"));

        javax.mail.Session session = javax.mail.Session.getInstance(prop);

        Message message = new MimeMessage(session);
        message.setFrom(
                new InternetAddress(properties.getProperty("mail.username")));
        message.setRecipients(
                Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject("Vaccination slots available for booking for 18+");

        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<head>");
        sb.append(
                "<style>table {font-family: arial, sans-serif;border-collapse: collapse;width: 100%;}");
        sb.append(
                "td, th {border: 2px solid #000000;text-align: center;padding: 8px;}th {background-color: #6b5b95;}");
        sb.append("tr {background-color: #e0e2e4;}</style>");
        sb.append("</head>");
        sb.append("<body>");
        sb.append(
                "<table><tr><th>Center Name</th><th>Available Slot Date</th></tr>");
        availabilities.stream().forEach(availability -> sb.append(String
                .format("<tr><td>%s</td><td>%s</td></tr>",
                        availability.getCenterName(), availability.getDate())));
        sb.append("</table>");
        sb.append("</body>");
        sb.append("</html>");

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(sb.toString(), "text/html; charset=utf-8");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        message.setContent(multipart);

        Transport t = session.getTransport("smtp");
        t.connect(properties.getProperty("mail.username"),
                properties.getProperty("mail.password"));
        t.sendMessage(message, message.getAllRecipients());
        t.close();
    }

    private void scheduleTask(String pinCode, String inputDate, String email,
            int N, Properties properties) {
        Runnable task = () -> {
            Set<Availability> availabilities = new HashSet<>();
            try {
                getNextNDates(inputDate, N).parallelStream().map(date -> {
                    try {
                        return getVaccinationSlotOnDate(pinCode, date);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }).filter(l -> l != null)
                        .forEach(list -> list.stream()
                                .forEach(av -> availabilities.add(av)));

                Supplier<TreeSet<Availability>> supplier = () -> new TreeSet<>(
                        Comparator.comparing(Availability::getCenterName)
                                .thenComparing(Availability::getDate));

                TreeSet<Availability> sortedAvailabilities = availabilities
                        .stream().collect(Collectors.toCollection(supplier));

                if (!sortedAvailabilities.isEmpty())
                    sendMail(email, sortedAvailabilities, properties);
            }
            catch (MessagingException e) {
                e.printStackTrace();
            }
        };
        ScheduledExecutorService executor = Executors
                .newSingleThreadScheduledExecutor();

        executor.scheduleAtFixedRate(task, 0, 15, TimeUnit.MINUTES);
    }

    public static void main(String[] args) throws IOException {
        String pinCode = args[0].trim();
        String inputDate = args[1].trim();
        String email = args[2].trim();
        int N = Integer.valueOf(args[3]);

        App app = new App();
        Properties properties = app.getProperties();

        app.scheduleTask(pinCode, inputDate, email, N, properties);

    }
}
