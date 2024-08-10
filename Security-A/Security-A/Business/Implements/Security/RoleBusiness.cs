using Business.Interfaces.Security;
using Data.Interfaces.Security;
using Entity.Dto;
using Entity.Dto.Security;
using Entity.Model.Security;

namespace Business.Implements.Security
{
    public class RoleBusiness : IRoleBusiness
    {
        private readonly IRoleData data;

        public RoleBusiness(IRoleData data)
        {
            this.data = data;
        }

        public async Task Delete(int id)
        {
            await data.Delete(id);
        }

        public async Task<IEnumerable<RoleDto>> GetAll()
        {
            IEnumerable<Role> roles = await data.GetAll();
            var roleDtos = roles.Select(role => new RoleDto
            {
                Id = role.Id,
                Name = role.Name,
                Description = role.Description,
                State = role.State
            });

            return roleDtos;
        }

        public async Task<IEnumerable<DataSelectDto>> GetAllSelect()
        {
            return await data.GetAllSelect();
        }

        public async Task<RoleDto> GetById(int id)
        {
            Role role = await data.GetById(id);
            RoleDto roleDto = new RoleDto();

            roleDto.Id = role.Id;
            roleDto.Name = role.Name;
            roleDto.Description = role.Description;
            roleDto.State = role.State;
            return roleDto;
        }

        public Role mapearDatos(Role role, RoleDto entity)
        {
            role.Id = entity.Id;
            role.Name = entity.Name;
            role.Description = entity.Description;
            role.State = entity.State;
            return role;
        }

        public async Task<Role> Save(RoleDto entity)
        {
            Role role = new Role();
            role = mapearDatos(role, entity);
            role.Created_at = DateTime.Now;
            role.Updated_at = null;
            role.Deleted_at = null;

            return await data.Save(role);
        }

        public async Task Update(RoleDto entity)
        {
            Role role = await data.GetById(entity.Id);
            if (role == null)
            {
                throw new Exception("Registro no encontrado");
            }
            role = mapearDatos(role, entity);
            role.Updated_at = DateTime.Now;

            await data.Update(role);
        }
    }
}
