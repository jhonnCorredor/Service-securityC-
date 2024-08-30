using Business.Interfaces.Security;
using Data.Interfaces.Security;
using Entity.Dto;
using Entity.Dto.Security;
using Entity.Model.Security;
using System.Text.Json;

namespace Business.Implements.Security
{
    public class UserBusiness : IUserBusiness
    {
        private readonly IUserData data;

        public UserBusiness(IUserData data)
        {
            this.data = data;
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
            User user = await data.GetById(id);
            UserDto UserDto = new UserDto();

            UserDto.Id = user.Id;
            UserDto.Username = user.Username;
            UserDto.Password = user.Password;
            UserDto.PersonId = user.PersonId;
            UserDto.State = user.State;
            return UserDto;
        }

        public async Task<User> Save(UserDto entity)
        {
            User user = new User();
            user = mapearDatos(user, entity);
            user.Created_at = DateTime.Now;
            user.Deleted_at = null;
            user.Updated_at = null;

            return await data.Save(user);
        }

        public async Task Update(UserDto entity)
        {
            User user = await data.GetById(entity.Id);
            if (user == null)
            {
                throw new Exception("Registro no encontrado");
            }
            user = mapearDatos(user, entity);
            user.Updated_at = DateTime.Now;

            await data.Update(user);
        }

        public async Task<IEnumerable<UserDto>> GetAll()
        {
            IEnumerable<User> users = await data.GetAll();
            var userDtos = users.Select(user => new UserDto
            {
                Id = user.Id,
                Username = user.Username,
                Password = user.Password,
                PersonId = user.PersonId,
                State = user.State
            });

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
