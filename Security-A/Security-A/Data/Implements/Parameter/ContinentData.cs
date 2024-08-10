using Data.Interfaces.Parameter;
using Entity.Context;
using Entity.Dto;
using Entity.Model.Parameter;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;

namespace Data.Implements.Parameter
{
    public class ContinentData : IContinentData
    {
        private readonly ApplicationDBContext context;
        protected readonly IConfiguration configuration;

        public ContinentData(ApplicationDBContext context, IConfiguration configuration)
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
            context.Continents.Update(entity);
            await context.SaveChangesAsync();
        }

        public async Task<IEnumerable<Continent>> GetAll()
        {
            var sql = @"SELECT * FROM Continents Where Deleted_at is null ORDER BY Id ASC";
            return await context.QueryAsync<Continent>(sql);
        }

        public async Task<IEnumerable<DataSelectDto>> GetAllSelect()
        {
            var sql = @"SELECT 
                        Id,
                        CONCAT(Code, ' - ', Name) AS TextoMostrar 
                    FROM 
                        Continents
                    WHERE Deleted_at IS NULL AND State = 1
                    ORDER BY Id ASC";
            return await context.QueryAsync<DataSelectDto>(sql);
        }

        public async Task<Continent> GetById(int id)
        {
            var sql = @"SELECT * FROM Continents WHERE Id = @Id ORDER BY Id ASC";
            return await context.QueryFirstOrDefaultAsync<Continent>(sql, new { Id = id });
        }

        public async Task<Continent> Save(Continent entity)
        {
            context.Continents.Add(entity);
            await context.SaveChangesAsync();
            return entity;
        }

        public async Task Update(Continent entity)
        {
            context.Entry(entity).State = EntityState.Modified;
            await context.SaveChangesAsync();
        }
    }
}
