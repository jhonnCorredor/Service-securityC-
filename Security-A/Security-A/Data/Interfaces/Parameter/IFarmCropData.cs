using Entity.Dto;
using Entity.Model.Parameter;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Data.Interfaces.Operational
{
    public interface IFarmCropData
    {
        Task Delete(int id);
        Task<IEnumerable<DataSelectDto>> GetAllSelect();
        Task<FarmCrop> GetById(int id);
        Task<FarmCrop> Save(FarmCrop entity);
        Task Update(FarmCrop entity);
        Task<IEnumerable<FarmCrop>> GetAll();
    }
}
