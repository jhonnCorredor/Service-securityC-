using Data.Interfaces.Operational;
using Entity.Context;
using Entity.Dto;
using Entity.Model.Operational;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;

namespace Data.Implements.Operational
{
    public class TreatmentData : ITreatmentData
    {
        private readonly ApplicationDBContext context;
        protected readonly IConfiguration configuration;

        public TreatmentData(ApplicationDBContext context, IConfiguration configuration)
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
            entity.DeletedAt = DateTime.Parse(DateTime.Today.ToString());
            entity.State = false;
            context.Treatments.Update(entity);
            await context.SaveChangesAsync();
        }

        public async Task<IEnumerable<DataSelectDto>> GetAllSelect()
        {
            var sql = @"SELECT 
                        Id,
                        TypeTreatment AS TextoMostrar 
                    FROM 
                        Treatments
                    WHERE DeletedAt IS NULL AND State = 1
                    ORDER BY Id ASC";
            return await context.QueryAsync<DataSelectDto>(sql);
        }

        public async Task<Treatment> GetById(int id)
        {
            var sql = @"SELECT * FROM Treatments WHERE Id = @Id ORDER BY Id ASC";
            return await context.QueryFirstOrDefaultAsync<Treatment>(sql, new { Id = id });
        }

        public async Task<Treatment> Save(Treatment entity)
        {
            context.Treatments.Add(entity);
            await context.SaveChangesAsync();
            return entity;
        }

        public async Task Update(Treatment entity)
        {
            context.Entry(entity).State = EntityState.Modified;
            await context.SaveChangesAsync();
        }

        public async Task<IEnumerable<Treatment>> GetAll()
        {
            var sql = @"SELECT * FROM Treatments Where DeletedAt is null ORDER BY Id ASC";
            return await context.QueryAsync<Treatment>(sql);
        }
    }
}
