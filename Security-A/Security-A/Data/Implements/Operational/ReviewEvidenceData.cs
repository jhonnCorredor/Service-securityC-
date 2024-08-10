using Data.Interfaces.Operational;
using Entity.Context;
using Entity.Dto;
using Entity.Model.Operational;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;

namespace Data.Implements.Operational
{
    public class ReviewEvidenceData : IReviewEvidenceData
    {
        private readonly ApplicationDBContext context;
        protected readonly IConfiguration configuration;

        public ReviewEvidenceData(ApplicationDBContext context, IConfiguration configuration)
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
            context.ReviewEvidences.Update(entity);
            await context.SaveChangesAsync();
        }

        public async Task<IEnumerable<DataSelectDto>> GetAllSelect()
        {
            var sql = @"SELECT 
                        Id,
                        State AS TextoMostrar 
                    FROM 
                        ReviewEvidences
                    WHERE Deleted_at IS NULL AND State = 1
                    ORDER BY Id ASC";
            return await context.QueryAsync<DataSelectDto>(sql);
        }

        public async Task<ReviewEvidence> GetById(int id)
        {
            var sql = @"SELECT * FROM ReviewEvidences WHERE Id = @Id ORDER BY Id ASC";
            return await context.QueryFirstOrDefaultAsync<ReviewEvidence>(sql, new { Id = id });
        }

        public async Task<ReviewEvidence> Save(ReviewEvidence entity)
        {
            context.ReviewEvidences.Add(entity);
            await context.SaveChangesAsync();
            return entity;
        }

        public async Task Update(ReviewEvidence entity)
        {
            context.Entry(entity).State = EntityState.Modified;
            await context.SaveChangesAsync();
        }

        public async Task<IEnumerable<ReviewEvidence>> GetAll()
        {
            var sql = @"SELECT * FROM ReviewEvidences Where Deleted_at is null ORDER BY Id ASC";
            return await context.QueryAsync<ReviewEvidence>(sql);
        }
    }
}
