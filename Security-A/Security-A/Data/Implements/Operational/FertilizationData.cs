using Data.Interfaces.Operational;
using Entity.Context;
using Entity.Dto;
using Entity.Model.Operational;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;

namespace Data.Implements.Operational
{
    public class FertilizationData : IFertilizationData
    {
        private readonly ApplicationDBContext context;
        protected readonly IConfiguration configuration;

        public FertilizationData(ApplicationDBContext context, IConfiguration configuration)
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
            context.Fertilizations.Update(entity);
            await context.SaveChangesAsync();
        }

        public async Task<IEnumerable<DataSelectDto>> GetAllSelect()
        {
            var sql = @"SELECT 
                        Id,
                        TypeFertilization AS TextoMostrar 
                    FROM 
                        Fertilizations
                    WHERE Deleted_at IS NULL AND State = 1
                    ORDER BY Id ASC";
            return await context.QueryAsync<DataSelectDto>(sql);
        }

        public async Task<Fertilization> GetById(int id)
        {
            var sql = @"SELECT * FROM Fertilizations WHERE Id = @Id ORDER BY Id ASC";
            return await context.QueryFirstOrDefaultAsync<Fertilization>(sql, new { Id = id });
        }

        public async Task<Fertilization> Save(Fertilization entity)
        {
            context.Fertilizations.Add(entity);
            await context.SaveChangesAsync();
            return entity;
        }

        public async Task Update(Fertilization entity)
        {
            context.Entry(entity).State = EntityState.Modified;
            await context.SaveChangesAsync();
        }

        public async Task<IEnumerable<Fertilization>> GetAll()
        {
            var sql = @"SELECT * FROM Fertilizations Where Deleted_at is null ORDER BY Id ASC";
            return await context.QueryAsync<Fertilization>(sql);
        }
    }
}
