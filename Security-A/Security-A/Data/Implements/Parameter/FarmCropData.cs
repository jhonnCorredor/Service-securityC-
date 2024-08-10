using Data.Interfaces.Operational;
using Entity.Context;
using Entity.Dto;
using Entity.Model.Parameter;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;

namespace Data.Implements.Parameter
{
    public class FarmCropData : IFarmCropData
    {
        private readonly ApplicationDBContext context;
        protected readonly IConfiguration configuration;

        public FarmCropData(ApplicationDBContext context, IConfiguration configuration)
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
            context.FarmCrops.Update(entity);
            await context.SaveChangesAsync();
        }

        public async Task<IEnumerable<DataSelectDto>> GetAllSelect()
        {
            var sql = @"SELECT 
                        Id,
                        Num_hectareas AS TextoMostrar 
                    FROM 
                        FarmCrops
                    WHERE Deleted_at IS NULL AND State = 1
                    ORDER BY Id ASC";
            return await context.QueryAsync<DataSelectDto>(sql);
        }

        public async Task<FarmCrop> GetById(int id)
        {
            var sql = @"SELECT * FROM FarmCrops WHERE Id = @Id ORDER BY Id ASC";
            return await context.QueryFirstOrDefaultAsync<FarmCrop>(sql, new { Id = id });
        }

        public async Task<FarmCrop> Save(FarmCrop entity)
        {
            context.FarmCrops.Add(entity);
            await context.SaveChangesAsync();
            return entity;
        }

        public async Task Update(FarmCrop entity)
        {
            context.Entry(entity).State = EntityState.Modified;
            await context.SaveChangesAsync();
        }

        public async Task<IEnumerable<FarmCrop>> GetAll()
        {
            var sql = @"SELECT * FROM FarmCrops Where Deleted_at is null ORDER BY Id ASC";
            return await context.QueryAsync<FarmCrop>(sql);
        }
    }
}
