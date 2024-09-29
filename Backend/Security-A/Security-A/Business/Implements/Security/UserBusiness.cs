using Business.Implements.Additional;
using Business.Interfaces.Additional;
using Business.Interfaces.Security;
using Data.Interfaces.Security;
using Entity.Dto;
using Entity.Dto.Security;
using Entity.Model.Security;
using Microsoft.Identity.Client;
using System.Text.Json;

namespace Business.Implements.Security
{
    public class UserBusiness : IUserBusiness
    {
        private readonly IUserData data;
        private readonly IUserRoleBusiness userRoleBusiness;
        private readonly IEmailBusiness emailBusiness;

        public UserBusiness(IUserData data, IUserRoleBusiness userRoleBusiness, IEmailBusiness emailBusiness)
        {
            this.data = data;
            this.userRoleBusiness = userRoleBusiness;
            this.emailBusiness = emailBusiness;
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

            EmailDto emailDto = new EmailDto
            {
                Para = email,
                Asunto = "Código de verificación para restablecer contraseña",
                Contenido = $"Estimado/a Usuario,\n\n" +
                $"Hemos recibido una solicitud para restablecer su contraseña. Su código de verificación es:\n\n" +
                $"**{codigoAleatorio}**\n\n" +
                "Por favor, use este código para continuar con el proceso de cambio de contraseña.\n\n" +
                "Si usted no solicitó este cambio, ignore este correo.\n\n" +
                "Saludos,\nEl equipo de soporte de FincAudita\n\n" +
                "*Este correo ha sido generado automáticamente, por favor no responda al mismo.*"
            };

            bool emailEnviado = await emailBusiness.SendEmail(emailDto);

            if (!emailEnviado)
            {
                throw new Exception("Error al enviar el correo");
            }

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

        public User mapearDatos(User user, UserDto entity)
        {
            user.Id = entity.Id;
            user.Username = entity.Username;
            user.Password = entity.Password;
            user.PersonId = entity.PersonId;
            user.State = entity.State;
            return user;
        }

        public async Task<IEnumerable<MenuDto>> Login(AuthenticationDto dto)
        {
            var login = await data.Login(dto.username, dto.password);
            List<MenuDto> menuDtos = new List<MenuDto>();
            
            foreach (var loginDto in login)
            {
                MenuDto menu = new MenuDto();
                menu.userID = loginDto.userID;
                menu.userName = loginDto.userName;
                menu.password = loginDto.password;
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
