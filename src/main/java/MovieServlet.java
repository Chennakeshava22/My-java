import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@WebServlet("/book")
public class MovieServlet extends HttpServlet {

    private static final Set<String> MOVIES = Set.of(
        "Interstellar", "Inception", "Avatar", "The Dark Knight"
    );

    @Override
    protected void doPost(HttpServletRequest req,
                          HttpServletResponse res) throws IOException {

        req.setCharacterEncoding("UTF-8");
        res.setContentType("text/html;charset=UTF-8");

        String movie = req.getParameter("movie");
        String name = req.getParameter("customerName");
        String email = req.getParameter("email");
        String date = req.getParameter("showDate");
        String time = req.getParameter("showTime");

        if (movie == null || !MOVIES.contains(movie)
                || name == null || name.isBlank()
                || name.length() > 80
                || email == null || email.length() > 120
                || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
                || date == null || !date.matches("\\d{4}-\\d{2}-\\d{2}")
                || time == null
                || !Set.of("10:00 AM", "01:30 PM",
                           "06:00 PM", "09:30 PM").contains(time)) {
            res.sendError(400, "Invalid booking details.");
            return;
        }

        try {
            java.time.LocalDate showDate =
                java.time.LocalDate.parse(date);

            if (showDate.isBefore(java.time.LocalDate.now())) {
                res.sendError(400, "Show date cannot be in the past.");
                return;
            }
        } catch (java.time.DateTimeException ex) {
            res.sendError(400, "Invalid show date.");
            return;
        }

        int price = switch (movie) {
            case "Interstellar" -> 250;
            case "Inception" -> 200;
            case "Avatar" -> 280;
            default -> 220;
        };

        String safeMovie = escape(movie);
        String safeName = escape(name.trim());
        String safeEmail = escape(email.trim());

        res.getWriter().println("""
            <!DOCTYPE html>
            <html>
            <head>
              <meta charset="UTF-8">
              <meta name="viewport" content="width=device-width, initial-scale=1">
              <title>Booking Confirmation</title>
              <style>
                body {
                  margin: 0; padding: 30px;
                  font-family: Arial, sans-serif;
                  background: linear-gradient(135deg,#100D24,#5426A8,#B72E86);
                  color: white; text-align: center;
                }
                .ticket {
                  max-width: 440px; margin: 45px auto;
                  padding: 30px; background: #241D40;
                  border: 1px solid #FF4D8D; border-radius: 16px;
                }
                h1 { color: #6EE7B7; }
                .pink { color: #FF80B0; }
                a {
                  display: inline-block; padding: 12px 22px;
                  margin-top: 20px; border-radius: 7px;
                  background: #FF4D8D; color: white;
                  text-decoration: none;
                }
              </style>
            </head>
            <body>
              <div class="ticket">
                <h1>✓ Booking Confirmed!</h1>
                <p>Your demo booking details</p>
                <hr>
                <h2 class="pink">%s</h2>
                <p>Customer: %s</p>
                <p>Email: %s</p>
                <p>Date: %s</p>
                <p>Showtime: %s</p>
                <h2 class="pink">Ticket Price: ₹%d</h2>
                <p>This is a demo. No payment was processed.</p>
                <a href="index.html">Back to Home</a>
              </div>
            </body>
            </html>
            """.formatted(safeMovie, safeName, safeEmail,
                          escape(date), escape(time), price));
    }

    private String escape(String value) {
        return value.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
    }
}
