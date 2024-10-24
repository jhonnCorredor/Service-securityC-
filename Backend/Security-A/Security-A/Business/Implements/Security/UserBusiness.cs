using Business.Implements.Additional;
using Business.Interfaces.Additional;
using Business.Interfaces.Security;
using Data.Interfaces.Security;
using Entity.Dto;
using Entity.Dto.Security;
using Entity.Model.Security;
using Microsoft.Identity.Client;
using System;
using System.Text.Json;
using static Dapper.SqlMapper;

namespace Business.Implements.Security
{
    public class UserBusiness : IUserBusiness
    {
        private readonly IUserData data;
        private readonly IUserRoleBusiness userRoleBusiness;
        private readonly IPersonBusiness personBusiness;
        private readonly IEmailBusiness emailBusiness;
        private readonly IWhatsappBusiness whatsappBusiness;

        public UserBusiness(IUserData data, IUserRoleBusiness userRoleBusiness, IEmailBusiness emailBusiness, IPersonBusiness personBusiness, IWhatsappBusiness whatsappBusiness)
        {
            this.data = data;
            this.userRoleBusiness = userRoleBusiness;
            this.emailBusiness = emailBusiness;
            this.personBusiness = personBusiness;
            this.whatsappBusiness = whatsappBusiness;
        }

        public async Task Delete(int id)
        {
            await data.Delete(id);
        }

        public async Task<IEnumerable<DataSelectDto>> GetAllSelect()
        {
            return await data.GetAllSelect();
        }

        public async Task<UserDto> GetById(int id)
        {
            UserDto user = await data.GetByIdAndRoles(id);
            UserDto userDto = new UserDto();

            userDto.Id = user.Id;
            userDto.Username = user.Username;
            userDto.Password = user.Password;
            userDto.PersonId = user.PersonId;
            userDto.State = user.State;
            if (user.roleString != null)
            {
                var options = new JsonSerializerOptions { PropertyNameCaseInsensitive = true };
                userDto.Roles = JsonSerializer.Deserialize<List<DataSelectDto>>(user.roleString, options);
            }

            return userDto;
        }

        public async Task<PasswordDto> GetByEmail(string email)
        {
            User user = await data.GetByEmail(email);
            PasswordDto passwordDto = new PasswordDto();
            Random random = new Random();

            if (user == null)
            {
                throw new Exception("Correo no registrado");
            }
            int codigoAleatorio = random.Next(1000, 10000);

            passwordDto.Id = user.Id;
            passwordDto.PersonId = user.PersonId;
            passwordDto.Code = codigoAleatorio.ToString();

            PersonDto person = await personBusiness.GetById(passwordDto.PersonId);

            EmailDto emailDto = new EmailDto
            {
                Para = email,
                Asunto = "Código de verificación para restablecer contraseña",
                Contenido = $@"
<!DOCTYPE html>
<html lang='es'>
<head>
    <meta charset='UTF-8'>
    <meta name='viewport' content='width=device-width, initial-scale=1.0'>
    <title>Código de verificación</title>
    <style>
        body {{
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 0;
            padding: 0;
        }}
        .container {{
            width: 100%;
            padding: 20px;
            text-align: center;
        }}
        .card {{
            background-color: white;
            max-width: 600px;
            margin: 0 auto;
            border-radius: 8px;
            border: 1px solid #ddd;
            text-align: center;
        }}
        .header {{
            background-color: #4CAF50;
            color: white;
            padding: 10px;
            font-size: 24px;
            border-radius: 8px 8px 0 0;
        }}
        .logo {{
            margin-top: 20px;
            width: 100%;
        }}
        .logo img {{
            display: block;
            margin: 0 auto;
        }}
        .content {{
            padding: 20px;
            font-size: 16px;
            color: #333;
        }}
        .code {{
            background-color: #4CAF50;
            color: white;
            font-size: 24px;
            font-weight: bold;
            padding: 10px;
            margin: 20px 0;
            display: inline-block;
            border-radius: 4px;
        }}
        .footer {{
            margin-top: 20px;
            font-size: 12px;
            color: #777;
        }}
    </style>
</head>
<body>
    <div class='container'>
        <div class='card'>
            <div class='header'>
                Código de verificación para restablecer contraseña
            </div>
            <table class='logo'>
                <tr>
                    <td style='text-align: center;'>
                        <img src='https://drive.google.com/uc?export=view&id=1qa4w5SGZrjrQOIinbCr9V9SwvRDgMHm2' alt='Logo FincAudita'>
                    </td>
                </tr>
            </table>
            <div class='content'>
                <p>Estimado/a Usuario, {user.Username} </p>
                <p>Hemos recibido una solicitud para restablecer su contraseña. Su código de verificación es:</p>
                <div class='code'>
                    {codigoAleatorio}
                </div>
                <p>Por favor, use este código para continuar con el proceso de cambio de contraseña.</p>
                <p>Si usted no solicitó este cambio, ignore este correo.</p>
                <p>Saludos,<br>El equipo de soporte de FincAudita</p>
            </div>
            <div class='footer'>
                <p>*Este correo ha sido generado automáticamente, por favor no responda al mismo.*</p>
            </div>
        </div>
    </div>
</body>
</html>"
            };

            bool emailEnviado = await emailBusiness.SendEmail(emailDto);

            if (!emailEnviado)
            {
                throw new Exception("Error al enviar el correo");
            }

            string mesage = await whatsappBusiness.EnviarMensajeAsync(person.Phone, "codigo_cambio_password", codigoAleatorio.ToString(), "https://drive.google.com/uc?export=view&id=1qa4w5SGZrjrQOIinbCr9V9SwvRDgMHm2");

            return passwordDto;
        }


        public async Task<User> Save(UserDto entity)
        {
            User user = new User();
            user = mapearDatos(user, entity);
            user.CreatedAt = DateTime.Now;
            user.State = true;
            user.DeletedAt = null;
            user.UpdatedAt = null;

            User save = await data.Save(user);

            if (entity.Roles != null && entity.Roles.Count > 0)
            {
                foreach (var role in entity.Roles)
                {
                    UserRoleDto userole = new UserRoleDto();
                    userole.UserId = save.Id;
                    userole.RoleId = role.Id;
                    userole.State = true;
                    await userRoleBusiness.Save(userole);
                }
            }

            PersonDto person = await personBusiness.GetById(save.PersonId);

            EmailDto emailDto = new EmailDto
            {
                Para = person.Email,
                Asunto = "¡Bienvenido a FincaAudita!",
                Contenido = $@"<!DOCTYPE html>
<html lang='es'>
<head>
    <meta charset='UTF-8'>
    <meta name='viewport' content='width=device-width, initial-scale=1.0'>
    <title>Bienvenido a FincaAudita</title>
    <style>
        body {{
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 0;
            background-color: #e6f7e6;
            width: 100% !important;
            -webkit-text-size-adjust: 100%;
            -ms-text-size-adjust: 100%;
        }}

        .container {{
            width: 100%;
            max-width: 800px;
            margin: 0 auto;
            background-color: white;
            border: 1px solid rgba(0, 0, 0, 0.5);
            border-radius: 10px;
            overflow: hidden;
        }}

        .header {{
            background-color: #4fca3c;
            text-align: center;
            padding: 20px 0;
        }}

        .header img {{
            width: 150px;
            height: auto;
            display: block;
            margin: 0 auto 10px; 
        }}

        h1 {{
            font-size: 24px;
            color: white;
            margin: 0;
        }}

        .content {{
            padding: 20px !important; 
        }}

        h2 {{
            font-size: 20px;
            color: #4fca3c;
        }}

        p {{
            font-size: 16px;
            line-height: 1.5;
            color: #555;
        }}

        .highlight {{
            color: #4fca3c;
            font-weight: bold;
        }}

        .btn {{
            display: inline-block;
            padding: 10px 20px;
            background-color: #4fca3c;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            font-size: 16px;
        }}

        .btn:hover {{
            background-color: #45a536;
        }}

        .footer {{
            background-color: #4fca3c;
            color: white;
            text-align: center;
            padding: 15px 0;
            font-size: 14px;
        }}

        img {{
            border: none;
            display: block;
            outline: none;
            text-decoration: none;
        }}

        a {{
            color: inherit;
            text-decoration: none;
        }}

        table {{
            border-collapse: collapse;
            mso-table-lspace: 0pt;
            mso-table-rspace: 0pt;
        }}

    </style>
</head>
<body>
    <div class='container'>
        <div class='header'>
            <img src='https://drive.google.com/uc?export=view&id=1BUOoxHyFXfz9d2LN35k1aRE2_BO2BG5H' alt='Logo FincAudita'>
            <h1>¡Bienvenido a FincaAudita!</h1>
        </div>
        <div class='content' style='padding: 20px;'>
            <h2>Hola, {save.Username} 👋</h2>
            <p>
                Nos complace darte la bienvenida a <span class='highlight'>FincaAudita</span>, 
                la plataforma que transformará la manera en que gestionas tus inspecciones agrícolas.
            </p>
            <p>
                Ahora puedes acceder a tu cuenta y comenzar a gestionar toda la información de tus inspecciones 
                de manera más rápida, eficiente y segura. Explora todas nuestras funcionalidades y comienza a 
                optimizar tus procesos hoy mismo.
            </p>
            <a href='#iniciar-sesion' class='btn'>Iniciar Sesión</a>
        </div>
        <div class='footer'>
            <p>&copy; 2024 FincaAudita | Todos los derechos reservados</p>
        </div>
    </div>  
</body>
</html>"
            };

            bool emailEnviado = await emailBusiness.SendEmail(emailDto);

            if (!emailEnviado)
            {
                throw new Exception("Error al enviar el correo");
            }


            string mesage = await whatsappBusiness.EnviarMensajeAsync(person.Phone, "bienvenida_fincaudita", save.Username, "https://drive.google.com/uc?export=view&id=1qa4w5SGZrjrQOIinbCr9V9SwvRDgMHm2");

            return save;

        }

        public async Task Update(UserDto entity)
        {
            User user = await data.GetById(entity.Id);
            if (user == null)
            {
                throw new Exception("Registro no encontrado");
            }
            user = mapearDatos(user, entity);
            user.UpdatedAt = DateTime.Now;

            await userRoleBusiness.DeleteRoles(user.Id);

            if (entity.Roles != null && entity.Roles.Count > 0)
            {
                foreach (var role in entity.Roles)
                {
                    UserRoleDto userole = new UserRoleDto();
                    userole.UserId = user.Id;
                    userole.RoleId = role.Id;
                    userole.State = true;
                    await userRoleBusiness.Save(userole);
                }
            }

            await data.Update(user);
        }

        public async Task<IEnumerable<UserDto>> GetAll()
        {
            IEnumerable<UserDto> users = await data.GetAll();
            List<UserDto> userDtos = new List<UserDto>();
            foreach (var user in users)
            {
                UserDto userDto = new UserDto();
                userDto.Id = user.Id;
                userDto.Username = user.Username;
                userDto.Password = user.Password;
                userDto.PersonId = user.PersonId;
                userDto.State = user.State;
                if (user.roleString != null)
                {
                    var options = new JsonSerializerOptions { PropertyNameCaseInsensitive = true };
                    userDto.Roles = JsonSerializer.Deserialize<List<DataSelectDto>>(user.roleString, options);
                }
                userDtos.Add(userDto);
            }
            return userDtos;
        }

        public async Task<IEnumerable<UserDto>> GetAllByRole(int id)
        {
            IEnumerable<UserDto> users = await data.GetAllByRole(id);
            List<UserDto> userDtos = new List<UserDto>();
            foreach (var user in users)
            {
                UserDto userDto = new UserDto();
                userDto.Id = user.Id;
                userDto.Username = user.Username;
                userDto.Password = user.Password;
                userDto.PersonId = user.PersonId;
                userDto.State = user.State;
                if (user.roleString != null)
                {
                    var options = new JsonSerializerOptions { PropertyNameCaseInsensitive = true };
                    userDto.Roles = JsonSerializer.Deserialize<List<DataSelectDto>>(user.roleString, options);
                }
                userDtos.Add(userDto);
            }
            return userDtos;
        }

        public User mapearDatos(User user, UserDto entity)
        {
            user.Id = entity.Id;
            if (!string.IsNullOrEmpty(entity.Photo))
            {
                user.Photo = Convert.FromBase64String(entity.Photo);
            }
            else
            {
                user.Photo = null; 
            }
            user.Username = entity.Username; 
            user.Password = BCrypt.Net.BCrypt.HashPassword(entity.Password);
            user.PersonId = entity.PersonId;
            user.State = entity.State;
            return user;
        }

        public async Task<IEnumerable<MenuDto>> Login(AuthenticationDto dto)
        {
            var login = await data.Login(dto.username);

            var user = login.First();

            if (login == null || !BCrypt.Net.BCrypt.Verify(dto.password, user.password))
            {
                throw new Exception("Usuario o contraseña incorrectos.");
            }

            List<MenuDto> menuDtos = new List<MenuDto>();
            
            foreach (var loginDto in login)
            {
                MenuDto menu = new MenuDto();
                menu.userID = loginDto.userID;
                menu.roleID = loginDto.roleID;
                menu.role = loginDto.role;
                if(loginDto.ListView != null)
                {
                    var options = new JsonSerializerOptions { PropertyNameCaseInsensitive = true };
                    menu.ListView = JsonSerializer.Deserialize<List<moduloDao>>(loginDto.ListView, options);
                }

                menuDtos.Add(menu);
            }

            return menuDtos;
        }
    }
}
