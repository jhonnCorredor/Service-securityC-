using Data.Interfaces.Operational;
using Entity.Context;
using Entity.Dto;
using Entity.Model.Security;
using Microsoft.Extensions.Configuration;

namespace Data.Implements.Operational
{
    public class LotFertilizationData : ILotFertilizationData
    {
        private readonly ApplicationDBContext context;
        protected readonly IConfiguration configuration;

        public LotFertilizationData(ApplicationDBContext context, IConfiguration configuration)
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
            context.LotFertilizations.Update(entity);
            await context.SaveChangesAsync();
        }

        public async Task<IEnumerable<DataSelectDto>> GetAllSelect()
        {
            var sql = @"SELECT 
                        Id,
                        CONCAT(Role_id, ' - ', View_id) AS TextoMostrar 
                    FROM 
                        LotFertilizations
                    WHERE Deleted_at IS NULL AND State = 1
                    ORDER BY Id ASC";
            return await context.QueryAsync<DataSelectDto>(sql);
        }

        public async Task<LotFertilization> GetById(int id)
        {
            var sql = @"SELECT * FROM LotFertilizations WHERE Id = @Id ORDER BY Id ASC";
            return await context.QueryFirstOrDefaultAsync<LotFertilization>(sql, new { Id = id });
        }

        public async Task<LotFertilization> Save(LotFertilization entity)
        {
            context.LotFertilizations.Add(entity);
            await context.SaveChangesAsync();
            return entity;
        }

        public async Task Update(LotFertilization entity)
        {
            context.Entry(entity).State = Microsoft.EntityFrameworkCore.EntityState.Modified;
            await context.SaveChangesAsync();
        }

        public async Task<IEnumerable<LotFertilization>> GetAll()
        {
            var sql = @"SELECT * FROM LotFertilizations Where Deleted_at is null ORDER BY Id ASC";
            return await context.QueryAsync<LotFertilization>(sql);
        }
    }
}
