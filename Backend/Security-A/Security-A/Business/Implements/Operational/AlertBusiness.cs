using Business.Interfaces.Operational;
using Data.Interfaces.Operational;
using Entity.Dto.Operational;
using Entity.Dto;
using Entity.Model.Operational;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Business.Interfaces.Security;
using Business.Interfaces.Additional;
using Entity.Dto.Security;

namespace Business.Implements.Operational
{
    public class AlertBusiness : IAlertBusiness
    {
        private readonly IAlertData data;
        private readonly IUserBusiness userBusiness;
        private readonly IPersonBusiness personBusiness;
        private readonly IWhatsappBusiness whatsappBusiness;
        private readonly IEmailBusiness emailBusiness;

        public AlertBusiness(IAlertData data, IUserBusiness userBusiness, IPersonBusiness personBusiness, IWhatsappBusiness whatsappBusiness, IEmailBusiness emailBusiness)
        {
            this.data = data;
            this.userBusiness = userBusiness;
            this.personBusiness = personBusiness;
            this.whatsappBusiness = whatsappBusiness;
            this.emailBusiness = emailBusiness;
        }

        public async Task Delete(int id)
        {
            await data.Delete(id);
        }

        public async Task<IEnumerable<AlertDto>> GetAll()
        {
            IEnumerable<Alert> Alerts = await data.GetAll();
            var AlertDtos = Alerts.Select(Alert => new AlertDto
            {
                Id = Alert.Id,
                Title = Alert.Title,
                Type = Alert.Type,
                Theme = Alert.Theme,
                Date = Alert.Date,
                UserId = Alert.UserId,
                State = Alert.State,
            });

            return AlertDtos;
        }

        public async Task<IEnumerable<AlertDto>> GetByUser(DataSelectDto dto)
        {
            IEnumerable<Alert> Alerts = await data.GetByUser(dto.Id);
            var AlertDtos = Alerts.Select(Alert => new AlertDto
            {
                Id = Alert.Id,
                Title = Alert.Title,
                Type = Alert.Type,
                Theme = Alert.Theme,
                Date = Alert.Date,
                UserId = Alert.UserId,
                State = Alert.State,
            });

            return AlertDtos;
        }

        public async Task<IEnumerable<DataSelectDto>> GetAllSelect()
        {
            return await data.GetAllSelect();
        }

        public async Task<AlertDto> GetById(int id)
        {
            Alert Alert = await data.GetById(id);
            AlertDto AlertDto = new AlertDto();
            AlertDto.Id = Alert.Id;
            AlertDto.Type = Alert.Type;
            AlertDto.Title = Alert.Title;
            AlertDto.Theme = Alert.Theme;
            AlertDto.Date = Alert.Date;
            AlertDto.UserId = Alert.UserId;
            AlertDto.State = Alert.State;
            return AlertDto;
        }

        public Alert mapearDatos(Alert Alert, AlertDto entity)
        {
            Alert.Id = entity.Id;
            Alert.Type = entity.Type;
            Alert.Title = entity.Title;
            Alert.Theme = entity.Theme;
            Alert.Date = entity.Date;
            Alert.UserId = entity.UserId;
            Alert.State = entity.State;
            return Alert;
        }

        public async Task<Alert> Save(AlertDto entity)
        {
            Alert Alert = new Alert();
            Alert = mapearDatos(Alert, entity);
            Alert.CreatedAt = DateTime.Now;
            Alert.State = true;
            Alert.UpdatedAt = null;
            Alert.DeletedAt = null;

            var user = await userBusiness.GetById(Alert.UserId);
            var person = await personBusiness.GetById(user.PersonId);

            DateTime fechaInicio = Alert.Date;
            DateTime fechaFin = Alert.Date.Date.AddHours(23).AddMinutes(59).AddSeconds(59);

            string fechaEntrada = fechaInicio.ToString("yyyyMMddTHHmmssZ");
            string fechaSalida = fechaFin.ToString("yyyyMMddTHHmmssZ");

            string descripcion = Alert.Type;
            string ciudad = "Neiva";
            string departamento = "Huila";
            string pais = "Colombia";
            string nombre = Alert.Title;

            string alertEmail = await emailBusiness.CrearEventoEnlaceAsync(fechaEntrada, fechaSalida, descripcion, ciudad, departamento, pais, nombre);

            EmailDto email = new EmailDto { 
                Para = person.Email,
                Asunto = Alert.Title,
                Contenido = alertEmail,
                };

            Boolean emailState = await emailBusiness.SendEmail(email);

            string alertMessage = $"{user.Username}, has recibido una nueva notificación: '{Alert.Title}' el {Alert.Date:dd/MM/yyyy}. Por favor revisa la aplicación para más detalles.";

            string mesage = await whatsappBusiness.EnviarMensajeAsync(person.Phone, "fincaudita_mesage", alertMessage, "https://drive.google.com/uc?export=view&id=1qa4w5SGZrjrQOIinbCr9V9SwvRDgMHm2");

            return await data.Save(Alert);
        }

        public async Task Update(AlertDto entity)
        {
            Alert Alert = await data.GetById(entity.Id);

            if (Alert == null)
            {
                throw new Exception("Registro no encontrado");
            }

            var user = await userBusiness.GetById(Alert.UserId);
            var person = await personBusiness.GetById(user.PersonId);

            string alertMessage = $"{user.Username}, la alerta con título '{Alert.Title}' y fecha '{Alert.Date:dd/MM/yyyy}' ha sido actualizada a un nuevo título '{entity.Title}' con la fecha '{entity.Date:dd/MM/yyyyy}'.";
            Alert = mapearDatos(Alert, entity);
            Alert.UpdatedAt = DateTime.Now;

            string mesage = await whatsappBusiness.EnviarMensajeAsync(person.Phone, "fincaudita_mesage", alertMessage, "https://drive.google.com/uc?export=view&id=1qa4w5SGZrjrQOIinbCr9V9SwvRDgMHm2");

            await data.Update(Alert);
        }
    }
}
