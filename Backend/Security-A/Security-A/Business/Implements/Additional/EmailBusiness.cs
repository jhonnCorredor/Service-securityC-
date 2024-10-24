using Entity.Dto.Security;
using Microsoft.Extensions.Configuration;
using MimeKit;
using MimeKit.Text;
using System.Net.Mail;
using MailKit.Net.Smtp;
using MailKit.Security;
using SmtpClient = MailKit.Net.Smtp.SmtpClient;
using Business.Interfaces.Additional;

namespace Business.Implements.Additional
{
    public class EmailBusiness: IEmailBusiness
    {
        private readonly IConfiguration _config;

        public EmailBusiness(IConfiguration config)
        {
            _config = config;
        }

        public async Task<bool> SendEmail(EmailDto request)
        {
            try
            {

                var email = new MimeMessage();

                email.From.Add(MailboxAddress.Parse(_config.GetSection("Email:UserName").Value));
                email.To.Add(MailboxAddress.Parse(request.Para));
                email.Subject = request.Asunto;
                email.Body = new TextPart(TextFormat.Html)
                {
                    Text = request.Contenido
                };

                using var smtp = new SmtpClient();
                smtp.Connect(
                    _config.GetSection("Email:Host").Value,
                    Convert.ToInt32(_config.GetSection("Email:Port").Value),
                    SecureSocketOptions.StartTls
                );

                smtp.Authenticate(_config.GetSection("Email:UserName").Value, _config.GetSection("Email:PassWord").Value);
                smtp.Send(email);
                smtp.Disconnect(true);

                return await Task.FromResult(true); 
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error al enviar correo: {ex.Message}");
                return await Task.FromResult(false);
            }
        }

        public async Task<string> CrearEventoEnlaceAsync(string fechaEntrada, string fechaSalida, string descripcion, string ciudad, string departamento, string pais, string nombre)
        {
            try
            {
                using (HttpClient client = new HttpClient())
                {
                    string ciudadEncoded = Uri.EscapeDataString(ciudad);
                    string departamentoEncoded = Uri.EscapeDataString(departamento);
                    string paisEncoded = Uri.EscapeDataString(pais);
                    string descripcionEncoded = Uri.EscapeDataString(descripcion);
                    string nombreEncoded = Uri.EscapeDataString(nombre);

                    string enlace = $"https://calendar.google.com/calendar/render?action=TEMPLATE&dates={fechaEntrada}%2F{fechaSalida}&details={descripcionEncoded}&location={ciudadEncoded}%2C%20{departamentoEncoded}%2C%20{paisEncoded}&text={nombreEncoded}";

                    HttpRequestMessage request = new HttpRequestMessage(HttpMethod.Get, enlace);

                    HttpResponseMessage response = await client.SendAsync(request);
                    response.EnsureSuccessStatusCode();
                    string responseBody = await response.Content.ReadAsStringAsync();

                    return enlace;
                }
            }
            catch (Exception ex)
            {
                return $"Error generando enlace del evento: {ex.Message}";
            }
        }
    }

}

