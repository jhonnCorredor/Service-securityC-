using Data.Interfaces.Operational;
using Entity.Context;
using Entity.Dto;
using Entity.Model.Operational;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;

namespace Data.Implements.Operational
{
    public class ReviewTechnicalData : IReviewTechnicalData
    {
        private readonly ApplicationDBContext context;
        protected readonly IConfiguration configuration;

        public ReviewTechnicalData(ApplicationDBContext context, IConfiguration configuration)
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
            context.ReviewTechnicals.Update(entity);
            await context.SaveChangesAsync();
        }

        public async Task<IEnumerable<DataSelectDto>> GetAllSelect()
        {
            var sql = @"SELECT 
                        Id,
                        Code AS TextoMostrar 
                    FROM 
                        ReviewTechnicals
                    WHERE Deleted_at IS NULL AND State = 1
                    ORDER BY Id ASC";
            return await context.QueryAsync<DataSelectDto>(sql);
        }

        public async Task<ReviewTechnical> GetById(int id)
        {
            var sql = @"SELECT * FROM ReviewTechnicals WHERE Id = @Id ORDER BY Id ASC";
            return await context.QueryFirstOrDefaultAsync<ReviewTechnical>(sql, new { Id = id });
        }

        public async Task<ReviewTechnical> Save(ReviewTechnical entity)
        {
            context.ReviewTechnicals.Add(entity);
            await context.SaveChangesAsync();
            return entity;
        }

        public async Task Update(ReviewTechnical entity)
        {
            context.Entry(entity).State = EntityState.Modified;
            await context.SaveChangesAsync();
        }

        public async Task<IEnumerable<ReviewTechnical>> GetAll()
        {
            var sql = @"SELECT * FROM ReviewTechnicals Where Deleted_at is null ORDER BY Id ASC";
            return await context.QueryAsync<ReviewTechnical>(sql);
        }
    }
}
