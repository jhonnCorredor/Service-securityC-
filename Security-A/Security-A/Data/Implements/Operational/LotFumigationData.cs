using Data.Interfaces.Operational;
using Entity.Context;
using Entity.Dto;
using Entity.Model.Security;
using Microsoft.Extensions.Configuration;

namespace Data.Implements.Operational
{
    public class LotFumigationData : ILotFumigationData
    {
        private readonly ApplicationDBContext context;
        protected readonly IConfiguration configuration;

        public LotFumigationData(ApplicationDBContext context, IConfiguration configuration)
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
            context.LotFumigations.Update(entity);
            await context.SaveChangesAsync();
        }

        public async Task<IEnumerable<DataSelectDto>> GetAllSelect()
        {
            var sql = @"SELECT 
                        Id,
                        CONCAT(Role_id, ' - ', View_id) AS TextoMostrar 
                    FROM 
                        LotFumigations
                    WHERE Deleted_at IS NULL AND State = 1
                    ORDER BY Id ASC";
            return await context.QueryAsync<DataSelectDto>(sql);
        }

        public async Task<LotFumigation> GetById(int id)
        {
            var sql = @"SELECT * FROM LotFumigations WHERE Id = @Id ORDER BY Id ASC";
            return await context.QueryFirstOrDefaultAsync<LotFumigation>(sql, new { Id = id });
        }

        public async Task<LotFumigation> Save(LotFumigation entity)
        {
            context.LotFumigations.Add(entity);
            await context.SaveChangesAsync();
            return entity;
        }

        public async Task Update(LotFumigation entity)
        {
            context.Entry(entity).State = Microsoft.EntityFrameworkCore.EntityState.Modified;
            await context.SaveChangesAsync();
        }

        public async Task<IEnumerable<LotFumigation>> GetAll()
        {
            var sql = @"SELECT * FROM LotFumigations Where Deleted_at is null ORDER BY Id ASC";
            return await context.QueryAsync<LotFumigation>(sql);
        }
    }
}
