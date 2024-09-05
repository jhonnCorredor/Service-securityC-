using Data.Interfaces.Security;
using Entity.Context;
using Entity.Dto;
using Entity.Dto.Security;
using Entity.Model.Security;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using System.Data;

namespace Data.Implements.Security
{
    public class UserData : IUserData
    {
        private readonly ApplicationDBContext context;
        protected readonly IConfiguration configuration;

        public UserData(ApplicationDBContext context, IConfiguration configuration)
        {
            this.context = context;
            this.configuration = configuration;
        }

        public async Task Delete(int id)
        {
            var entity = await GetById(id);
            if (entity == null)
            {
                throw new Exception("Registro no encontrado");
            }
            entity.Deleted_at = DateTime.Parse(DateTime.Today.ToString());
            entity.State = false;
            context.Users.Update(entity);
            await context.SaveChangesAsync();
        }

        public async Task<IEnumerable<DataSelectDto>> GetAllSelect()
        {
            var sql = @"SELECT 
                        Id,
                        CONCAT(Username, ' - ', Password) AS TextoMostrar 
                    FROM 
                        Users
                    WHERE Deleted_at IS NULL AND State = 1
                    ORDER BY Id ASC";
            return await context.QueryAsync<DataSelectDto>(sql);
        }

        public async Task<User> GetById(int id)
        {
            var sql = @"SELECT * FROM Users WHERE Id = @Id ORDER BY Id ASC";
            return await context.QueryFirstOrDefaultAsync<User>(sql, new { Id = id });
        }

        public async Task<User> Save(User entity)
        {
            context.Users.Add(entity);
            await context.SaveChangesAsync();
            return entity;
        }

        public async Task Update(User entity)
        {
            context.Entry(entity).State = EntityState.Modified;
            await context.SaveChangesAsync();
        }

        public async Task<User> GetByUsername(string username)
        {
            return await context.Users.AsNoTracking().Where(item => item.Username == username).FirstOrDefaultAsync();
        }

        public async Task<User> GetByPassword(string password)
        {
            return await context.Users.AsNoTracking().Where(item => item.Password == password).FirstOrDefaultAsync();
        }

        public async Task<IEnumerable<User>> GetAll()
        {
            var sql = @"SELECT * FROM Users Where Deleted_at is null ORDER BY Id ASC";
            return await context.QueryAsync<User>(sql);
        }

        public async Task<IEnumerable<LoginDto>> Login(string username, string password)
        {
            var sql = @"
                SELECT 
                    u.Id AS userID,
                    u.Username,
                    u.Password,
                    r.Id AS roleID,
                    r.Name AS role,
                    (
		                SELECT
			                m.Name AS Modulo,
                            m.Description AS ModuloDescription,
			                (
				                SELECT 
					                v.Id,
					                v.Name,
					                v.Route,
                                    v.Description
				                FROM Views AS v
				                INNER JOIN RoleViews AS rv ON rv.ViewId = v.Id
				                WHERE v.Deleted_at IS null AND rv.RoleId = r.Id AND v.ModuloId = m.Id
				                GROUP BY v.Id, v.Name, v.Route, v.Description
				                FOR JSON PATH
			                ) AS views
		                FROM Modulos AS m
		                INNER JOIN Views AS v ON v.ModuloId = m.Id
		                INNER JOIN RoleViews AS rv ON rv.ViewId = v.Id
                        WHERE m.Id = v.ModuloId AND rv.RoleId = r.Id 
                        GROUP BY m.Id, m.Name, m.Position, m.Description
                        ORDER BY m.Position ASC
		                FOR JSON PATH
                    ) AS ListView
                FROM Users AS u
                LEFT JOIN UserRoles AS ur ON ur.UserId = u.Id
                LEFt JOIN Roles AS r ON r.Id = ur.RoleId
                WHERE u.Username = @Username AND u.Password = @Password
                AND u.Deleted_at is null
                GROUP BY u.Id, u.Username, u.Password, r.Id, r.Name;
            ";

            return await context.QueryAsync<LoginDto>(sql, new { Username = username, Password = password });
        }
    }
}
