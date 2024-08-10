using Data.Interfaces.Operational;
using Entity.Context;
using Entity.Dto;
using Entity.Model.Parameter;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;

namespace Data.Implements.Parameter
{
    public class FertilizationSuppliesData : IFertilizationSuppliesData
    {
        private readonly ApplicationDBContext context;
        protected readonly IConfiguration configuration;

        public FertilizationSuppliesData(ApplicationDBContext context, IConfiguration configuration)
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
            context.FertilizationSupplies.Update(entity);
            await context.SaveChangesAsync();
        }

        public async Task<IEnumerable<DataSelectDto>> GetAllSelect()
        {
            var sql = @"SELECT 
                        Id,
                        Dose AS TextoMostrar 
                    FROM 
                        FertilizationSupplies
                    WHERE Deleted_at IS NULL AND State = 1
                    ORDER BY Id ASC";
            return await context.QueryAsync<DataSelectDto>(sql);
        }

        public async Task<FertilizationSupplies> GetById(int id)
        {
            var sql = @"SELECT * FROM FertilizationSupplies WHERE Id = @Id ORDER BY Id ASC";
            return await context.QueryFirstOrDefaultAsync<FertilizationSupplies>(sql, new { Id = id });
        }

        public async Task<FertilizationSupplies> Save(FertilizationSupplies entity)
        {
            context.FertilizationSupplies.Add(entity);
            await context.SaveChangesAsync();
            return entity;
        }

        public async Task Update(FertilizationSupplies entity)
        {
            context.Entry(entity).State = EntityState.Modified;
            await context.SaveChangesAsync();
        }

        public async Task<IEnumerable<FertilizationSupplies>> GetAll()
        {
            var sql = @"SELECT * FROM FertilizationSupplies Where Deleted_at is null ORDER BY Id ASC";
            return await context.QueryAsync<FertilizationSupplies>(sql);
        }
    }
}
